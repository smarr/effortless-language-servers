"use strict";

import { BreakpointEvent, DebugSession, Handles, InitializedEvent, Scope,
  Source, StackFrame, StoppedEvent, Thread, Variable } from 'vscode-debugadapter';
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

  export interface WDSource {
    id:   string;
    name: string;
    uri:  string;
    sourceText?: string;
    mimeType?:   string;

    vsSource?: Source;
  }
}

interface BreakpointPair {
  vs: DebugProtocol.Breakpoint;
  som: WebDebugger.Breakpoint;
}

class SomDebugSession extends DebugSession {
  private socket: WebSocket;
  private breakpoints: BreakpointPair[];
  private nextBreakpointId: number;
  private bufferedBreakpoints: WebDebugger.Breakpoint[];
  private sources: WebDebugger.WDSource[];

  private lastSuspendEvent;
  private varHandles : Handles<string>;

  public constructor() {
    super();

    this.socket = null;

    this.breakpoints = [];
    this.nextBreakpointId = 0;
    this.bufferedBreakpoints = [];

    this.sources = [];

    this.lastSuspendEvent = null;
    this.varHandles = new Handles<string>();

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

  protected launchRequest(response: DebugProtocol.LaunchResponse,
      args: LaunchRequestArguments): void {
    // TODO
  }

  protected attachRequest(response: DebugProtocol.AttachResponse,
      args: AttachRequestArguments): void {
    this.socket = new WebSocket('ws://localhost:' + args.port);

    this.socket.on('open', () => {
      this.sendInitialBreakpoints();
    });

    this.socket.onmessage = this.onWDMessage.bind(this);
    
    this.sendResponse(response);

    // TODO:
    // - implement setting breakpoints
    // - implement stepping 
  }

  private onWDMessage(event): void {
    const data = JSON.parse(event.data);
    
    switch (data.type) {
      case "source":
        this.onSource(data);
        break;
      case "suspendEvent":
        this.onSuspendEvent(data);
        break;
    }
  }

  private onSource(data): void {
    for (let id in data.sources) {
      console.log("onSource id", id);
      this.sources[id] = data.sources[id];
    }
  }

  private onSuspendEvent(data): void {
    this.sendEvent(new StoppedEvent("breakpoint", 1));
    this.lastSuspendEvent = data;
  }

  private sendInitialBreakpoints() : void {
    const updatedBreakpoints = [];
    const initialBreakpoints = [];
    
    for (const id in this.bufferedBreakpoints) {
      const vsBp = this.breakpoints[id].vs;
      vsBp.verified = true;
      updatedBreakpoints.push(vsBp);
      initialBreakpoints.push(this.bufferedBreakpoints[id]);
    }
    const msg = JSON.stringify({
        action: "initialBreakpoints", breakpoints: initialBreakpoints});
    this.socket.send(msg);
    
    for (const vsBp of updatedBreakpoints) {
      this.sendEvent(new BreakpointEvent('Connection Established', vsBp));
    }
  }

  protected setBreakPointsRequest(response: DebugProtocol.SetBreakpointsResponse,
      args: DebugProtocol.SetBreakpointsArguments): void {
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
      this.socket.send(JSON.stringify({
        action: "updateBreakpoint",
        breakpoint: bp
      }));
    } else {
      this.bufferedBreakpoints[bpId] = bp;
    }
  }

  protected threadsRequest(response: DebugProtocol.ThreadsResponse): void {
    // TODO: add some form of support for actors. not sure yet what would be
    //       useful. A list of actors isn't very useful for fine-grained ones
    response.body = {
      threads: [ new Thread(1, "thread 1") ]
    };
    this.sendResponse(response);
  }

  protected stackTraceRequest(response: DebugProtocol.StackTraceResponse,
      args: DebugProtocol.StackTraceArguments): void {
    const startFrame = (args.startFrame != null) ? args.startFrame : 0;
    const numFrames  = (args.levels != null) ? args.levels : 0;
    const endFrame   = Math.min(startFrame + numFrames, this.lastSuspendEvent.stack.length);

    const stackFrames = [];
    for (let i = startFrame; i < endFrame; i += 1) {
      const frame = this.lastSuspendEvent.stack[i];

      console.log("stackTraceRequest this.sources: ", this.sources);
      console.log("stackTraceRequest ss: ", frame.sourceSection);
      console.log("stackTraceRequest sourceId: ", frame.sourceSection.sourceId);
      let s = this.sources[frame.sourceSection.sourceId];
      let source;
      if (s) {
        console.log("stackTraceRequest: ", s);
        if (s.vsSource) {
          source = s.vsSource;
        } else {
          console.log("stackTraceRequest s.uri", s.uri);
          source = new Source(s.name, s.uri.substring("file:".length), parseInt(s.id));
        }
      } else {
        source = null;
      }

      stackFrames.push(new StackFrame(i, frame.methodName, source,
        frame.sourceSection.line, frame.sourceSection.column));
    }

    response.body = {
      stackFrames: stackFrames,
      totalFrames: this.lastSuspendEvent.stack.length
    };
    this.sendResponse(response);
  }

  protected scopesRequest(response: DebugProtocol.ScopesResponse,
      args: DebugProtocol.ScopesArguments): void {
    const frameRef = args.frameId;
    const scopes = [
      new Scope("Local",   this.varHandles.create("local_" + frameRef),   false),
      new Scope("Closure", this.varHandles.create("closure_" + frameRef), false),
      new Scope("Outer",   this.varHandles.create("outer_" + frameRef),   true)
    ];
    response.body = {
      scopes : scopes
    };
    this.sendResponse(response);
  }

  protected variablesRequest(response: DebugProtocol.VariablesResponse,
      args: DebugProtocol.VariablesArguments): void {
    const ref = this.varHandles.get(args.variablesReference).split("_");
    const id = parseInt(ref[1]);
    const variables = [];

    switch (ref[0]) {
      case "local":
        const frame = this.lastSuspendEvent.topFrame;
        if (id == 0) { // we only got the data for the top frame
          for (let aI in frame.arguments) {
            const v = new Variable(aI, frame.arguments[aI]);
            variables.push(v);
          }

          for (let sI in frame.slots) {
            const v = new Variable(sI, frame.slots[sI]);
            variables.push(v);
          }
        }
        break;
    }
    
    response.body = {
      variables: variables
    };
    this.sendResponse(response);
  }
}

DebugSession.run(SomDebugSession);
