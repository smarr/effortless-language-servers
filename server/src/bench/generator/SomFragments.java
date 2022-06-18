package bench.generator;

public class SomFragments {

  public static String[] methods = new String[] {
      """
          %s: path name: fileName into: systemClass in: universe = (
             | fname parser result cname |
             fname := path + '/' + fileName + '.som'.

             parser := Parser load: fname in: universe.
             parser ifNil: [ ^ nil ].

             result := self compile: parser into: systemClass.

             cname := result name string.

             fileName ~= cname ifTrue: [
               self error: 'File name ' + fname
                   + ' does not match class name (' + cname + ') in it.' ].
             ^ result
           )
           """,
      """
          %s: stmt into: systemClass in: universe = (
               | parser |
               parser := Parser newWith: stmt for: '$string$' in: universe.
               ^ self compile: parser into: systemClass.
             )
           """,
      """
          %s: parser into: systemClass = (
             | cgc |
             cgc := parser classdef.

             systemClass == nil
               ifTrue: [ ^ cgc assemble ]
               ifFalse: [ ^ cgc assembleSystemClass: systemClass ]
           )
           """,
      """
          %s: universe = (
              ^ universe arrayClass
            )
          """,
      """
          %s: idx = (
              ^ indexableFields at: idx
            )
          """,
      """
          %s: idx put: val = (
              ^ indexableFields at: idx put: val
            )
          """,
      """
          %s = (
            ^ indexableFields length
          )
          """,
      """
          %s: destination = (
              indexableFields doIndexes: [:i |
                destination indexableField: i put: (indexableFields at: i) ]
            )
          """,
      """
            "For using in debugging tools such as the Diassembler"
          %s = (
              | elems |
              elems := ''.
              indexableFields do: [:e |
                elems = '' ifTrue: [elems := e debugString]
                           ifFalse: [ elems := elems + ', ' + e debugString] ].
               ^ 'SArray(' + indexableFields length + '; ' + elems + ')' )
          """,
      """
          %s: length with: nilObject = (
            ^ self new initializeWith: length and: nilObject
          )
          """,
      """
            %s = (
              ^ false
            )
          """,
      """
            %s = (
              ^ numberOfLocals
            )
          """,
      """
            %s = (
              ^ maximumNumberOfStackElements
            )
          """,
      """
            %s = (
              ^ signature
            )
          """,
      """
          %s = (
            ^ holder
          )
          """,
      """
            %s: bytecodeIndex = (
              "Get the constant associated to a given bytecode index"
              ^ literals at: (bytecodes at: bytecodeIndex + 1)
            )
          """,
      """
            %s = (
              "Get the number of arguments of this method"
              ^ signature numberOfSignatureArguments
            )
          """,
      """
            %s = (
              "Get the number of bytecodes in this method"
              ^ bytecodes length
            )
          """,
      """
            %s: index = (
              "Get the bytecode at the given index"
              ^ bytecodes at: index
            )
          """,
      """
            %s: frame using: interpreter = (
              | newFrame |
              "Allocate and push a new frame on the interpreter stack"
              newFrame := interpreter pushNewFrame: self.
              newFrame copyArgumentsFrom: frame
            )
          """,
      """
            %s: universe = (
              ^ universe methodClass
            )
          """,
      """
          "For using in debugging tools such as the Diassembler"
          %s = ( ^ 'SMethod(' + holder name + '>>#' + signature string + ')' )
          """,
      """
          %s: size = (
              | sum byteAcc bitNum y |
              sum     := 0.
              byteAcc := 0.
              bitNum  := 0.

              y := 0.

              [y < size] whileTrue: [
                  | ci x |
                  ci := (2.0 * y // size) - 1.0.
                  x  := 0.

                  [x < size] whileTrue: [
                      | zr zrzr zi zizi cr escape z notDone |
                      zrzr := zr := 0.0.
                      zizi := zi := 0.0.
                      cr   := (2.0 * x // size) - 1.5.

                      z := 0.
                      notDone := true.
                      escape := 0.
                      [notDone and: [z < 50]] whileTrue: [
                          zr := zrzr - zizi + cr.
                          zi := 2.0 * zr * zi + ci.

                          "preserve recalculation"
                          zrzr := zr * zr.
                          zizi := zi * zi.

                          (zrzr + zizi > 4.0) ifTrue: [
                              notDone := false.
                              escape  := 1.
                          ].
                          z := z + 1.
                      ].

                      byteAcc := (byteAcc << 1) + escape.
                      bitNum  := bitNum + 1.

                      " Code is very similar for these cases, but using separate blocks
                        ensures we skip the shifting when it's unnecessary,
                        which is most cases. "
                      bitNum = 8
                          ifTrue: [
                            sum := sum bitXor: byteAcc.
                            byteAcc := 0.
                            bitNum  := 0. ]
                          ifFalse: [
                            (x = (size - 1)) ifTrue: [
                                byteAcc := byteAcc << (8 - bitNum).
                                sum := sum bitXor: byteAcc.
                                byteAcc := 0.
                                bitNum  := 0. ]].
                      x := x + 1.
                  ].
                  y := y + 1.
              ].

              ^ sum
          )
          """,
      """
            %s: argument  = primitive
          """,
      """
              %s: argument=primitive "modulo with sign of dividend"
          """,
      """
          %s         = ( ^(self < 0) ifTrue: (0 - self) ifFalse: self )
          """,
      """
              %s     = ( ^0 - self )
          """,
      """
              %s: exponent = (
                  "Raise the receiver to the given exponent.
                   Currently only positive integer exponents
                   are fully supported."
                  | output |
                  output := 1.
                  exponent asInteger
                    timesRepeat: [ output := output * self ].
                  ^ output
              )
          """,
      """
              "Random numbers"
              %s = primitive
          """,
      """
              "Comparing"
              %s: argument = ( ^self = argument )
          """,
      """
              %s: argument = ( ^(self = argument) not )
          """,
      """
              %s:  argument = ( ^(self >= argument) and: [ self <> argument ] )
          """,
      """
              %s: argument = ( ^(self < argument) not )
          """,
      """
              %s: argument = ( ^(self < argument) or: [ self = argument ] )
          """,
      """
              %s    = ( ^self < 0 )
          """,
      """
              %s: a and: b = ( ^(self > a) and: [ self < b ] )
          """,
      """
              "Iterating"
              %s: limit do: block = (
                  self to: limit by: 1 do: block
              )
          """,
      """
              %s: limit by: step do: block = (
                  | i |
                  i := self.
                  [ i <= limit ] whileTrue: [ block value: i. i := i + step ]
              )
          """,
      """
              %s: limit do: block = (
                  self downTo: limit by: 1 do: block
              )
          """,
      """
              %s: limit by: step do: block = (
                  | i |
                  i := self.
                  [ i >= limit ] whileTrue: [ block value: i. i := i - step ]
              )
          """,
      """
              "More Iterations"
              %s: block = (
                  1 to: self do: [ :i | block value ]
              )
          """,
      """
              "Range Creation"
              %s: upper = (
                  | range |
                  range := Array new: upper - self + 1.
                  self to: upper do: [ :i | range at: i put: i ].
                  ^range
              )
          """,
      """
              %s: otherInt = (
                  (self < otherInt) ifTrue: [^otherInt] ifFalse: [^self].
              )
          """,
      """
          %s: otherInt = (
              (self > otherInt) ifTrue: [^otherInt] ifFalse: [^self].
          )
                """
  };

}
