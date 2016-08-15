"use strict";

import { DebugSession, InitializedEvent, BreakpointEvent, StoppedEvent }
  from 'vscode-debugadapter';
import { DebugProtocol } from 'vscode-debugprotocol';
import * as WebSocket from 'ws';

export interface LaunchRequestArguments extends DebugProtocol.LaunchRequestArguments {
  /** Path to the main program */
  program: string;

  /** Workding directory */
  cwd: string;

  /** Automatically stop after launch */
  stopOnEntry?: boolean;

  /** Path to the SOMns interpreter */
  runtime?: string;

  /** Optional interpreter arguments */
  runtimeArgs?: string[];
}

export interface AttachRequestArguments extends DebugProtocol.AttachRequestArguments {
  /** Debugger port of the interpreter */
  port: number
}

namespace WebDebugger {
  export class Breakpoint {
    public type:      string;
    public sourceUri: string;
    public enabled:   boolean;

    constructor(type: string, uri: string, enabled: boolean) {
      this.type      = type;
      this.sourceUri = uri;
      this.enabled   = enabled;
    }
  }

  export class LineBreakpoint extends Breakpoint {
    public line: number;

    constructor(uri: string, enabled: boolean, line: number) {
      super("lineBreakpoint", uri, enabled);
      this.line = line;
    }
  }
}

interface BreakpointPair {
  vs: DebugProtocol.Breakpoint;
  som: WebDebugger.Breakpoint;
}

class SomDebugSession extends DebugSession {
  private socket: WebSocket;
  private breakpoints: BreakpointPair[];
  private nextBreakpointId : number;
  private bufferedBreakpoints : WebDebugger.Breakpoint[];

  public constructor() {
    super();

    this.socket = null;

    this.breakpoints = [];
    this.bufferedBreakpoints = [];
    this.nextBreakpointId = 0;

    // Truffle source sections use 1-based indexes
    this.setDebuggerColumnsStartAt1(true);
    this.setDebuggerLinesStartAt1(true);
  }

  protected initializeRequest(response: DebugProtocol.InitializeResponse,
      args: DebugProtocol.InitializeRequestArguments): void {
    this.sendEvent(new InitializedEvent());

    response.body.supportsConfigurationDoneRequest = true;
    response.body.supportsEvaluateForHovers = true;
    response.body.supportsRestartFrame = true;
    response.body.supportsStepBack = false; // TODO: figure out how to support

    // TODO: https://github.com/Microsoft/vscode-debugadapter-node/pull/44
    // response.body.supportsStepInTargetsRequest

    this.sendResponse(response);
  }

  protected launchRequest(response: DebugProtocol.LaunchResponse, args: LaunchRequestArguments): void {
    // TODO
  }

  protected attachRequest(response: DebugProtocol.AttachResponse,
      args: AttachRequestArguments): void {
    this.socket = new WebSocket('ws://localhost:' + args.port);

    this.socket.on('open', () => {
      this.sendInitialBreakpoints();
    });

    this.socket.onmessage = this.onWDMessage;
    
    this.sendResponse(response);

    // TODO:
    // - implement setting breakpoints
    // - implement stepping 
  }

  private sendInitialBreakpoints() : void {
    console.log("sendInitialBreakpoints: " + JSON.stringify(this.bufferedBreakpoints));
    const updatedBreakpoints = [];
    const initialBreakpoints = [];
    
    for (const id in this.bufferedBreakpoints) {
      console.log("sendInitialBreakpoints id: " + id);
      const vsBp = this.breakpoints[id].vs;
      vsBp.verified = true;
      updatedBreakpoints.push(vsBp);
      initialBreakpoints.push(this.bufferedBreakpoints[id]);
    }
    const msg = JSON.stringify({
        action: "initialBreakpoints", breakpoints: initialBreakpoints});
    this.socket.send(msg);
    console.log("Sent initialBreakpoints message: " + msg);
    
    for (const vsBp of updatedBreakpoints) {
      this.sendEvent(new BreakpointEvent('Connection Established', vsBp));
    }
  }

  protected setBreakPointsRequest(response: DebugProtocol.SetBreakpointsResponse,
      args: DebugProtocol.SetBreakpointsArguments): void {
    console.log("setBreakPointsRequest: " + JSON.stringify(args));

    const connected = this.socket != null;
    const uri = "file:" + args.source.path;
    const breakpoints = [];
    response.body = {breakpoints: breakpoints};

    for (let bp of args.breakpoints) {
      const bpId = this.getNextBpId();
      const lineBp = new WebDebugger.LineBreakpoint(uri, true, bp.line);
      this.sendBreakpoint(lineBp, bpId, connected);
      
      const vsBp = {
        id:       bpId,
        verified: connected,
        source:   args.source,
        line:     bp.line
      }
      this.breakpoints[vsBp.id] = {vs: vsBp, som: lineBp};

      breakpoints.push(vsBp);
    }
    this.sendResponse(response);
  }

  private getNextBpId(): number {
    const id = this.nextBreakpointId;
    this.nextBreakpointId += 1;
    return id;
  }

  private sendBreakpoint(bp: WebDebugger.Breakpoint, bpId: number,
      connected: boolean): void {
    if (connected) {
      console.log("sendBreakpoint: " + JSON.stringify(bp));
      this.socket.send(JSON.stringify({
        action: "updateBreakpoint",
        breakpoint: bp
      }));
    } else {
      console.log("Buffer breakpoint: " + JSON.stringify(bp));
      this.bufferedBreakpoints[bpId] = bp;
    }
  }
}

DebugSession.run(SomDebugSession);
