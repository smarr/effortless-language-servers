import { integer } from "vscode-languageserver-protocol";
import {Message as OrigMessage} from "./messages";

export type Message = OrigMessage | UpdateClass | RestartFrame

/* This file adds extra messages than the ones defined in extension.ts as that file is a symbolic link to another repository */
export interface UpdateClass {
  action: "UpdateClass";
  classToRecompile: String;
}

export interface RestartFrame {
  action: "RestartFrame";
  frameId: integer;
}
