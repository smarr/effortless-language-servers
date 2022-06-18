package bench.generator;

public class NewspeakFragments {

  public static String[] methodFragments = new String[] {
      """
          (* return buffer array *)
              public %s = (
                ^ vmMirror fileBuffer: self.
              )
                    """,
      """
              (* set access mode, can be #read, #write, or #readWrite *)
              public %s: m <Symbol> = (
                vmMirror file: self setMode: m.
              )
          """, """
              (* return size of the internal buffer *)
              public %s = (
                ^ vmMirror fileBufferSize: self.
              )
          """,
      """
              (* set size for the interal buffer, only works before opening the file *)
              public %s: size = (
                (vmMirror file: self setBufferSize: size) ifFalse: [
                  self error: 'File already open! Buffer size can be changed for closed files only'.
                ].
              )
          """,
      """
              (* close file *)
              public %s = (
                vmMirror fileClose: self.
              )
          """,
      """
              (* read as many bytes as possible into the buffer (up to its size),
                 and return the number of bytes that were actually read.
                 The semantics at end of file are to return 0 with no error. *)
              public %s: pos <Integer> ^ <Integer> = (
                ^ readAt: pos ifFail: [:s |
                    IOException signalWith: 'Reading from file failed. File: ' + #TODO + ' ' + s ]
              )
          """,
      """
              (* read as many bytes as possible into the buffer (up to its size),
                 and return the number of bytes that were actually read.
                 The semantics at end of file are to return 0 with no error. *)
              public %s: pos <Integer> ifFail: errf <[:Symbol]> ^ <Integer> = (
                ^ vmMirror file: self readAt: pos ifFail: errf.
              )
          """,
      """
              public %s: nBytes <Integer> at: pos <Integer> = (
                ^ write: nBytes at: pos ifFail: [:s | 'Writing to file failed. File: ' + #TODO + ' ' + s ]
              )
          """,
      """
              public %s: nBytes <Integer> at: pos <Integer> ifFail: errf <[:IOException]>  = (
                vmMirror file: self write: nBytes at: pos ifFail: errf.
              )
          """,
      """
              public %s ^ <Integer> = (
                ^ vmMirror fileSize: self.
              )
          """, """
              public %s ^ <Boolean> = (
                ^ vmMirror fileIsClosed: self.
              )
          """, """
              public %s: errBlock <[:Symbol]> = (
                ^ vmMirror file: self openIfFail: errBlock.
              )
          """, """
               (* Concatenation of FilePatterns *)
              public %s: extension <FilePattern> ^ <EXTENDED> = (
                ^ extension extendPattern: self
              )
          """, """
              (* returns the seperator *)
              public %s = (
                ^ vmMirror pathSeparator: self.
              )
          """, """
              (* extend pattern *)
              public %s: string = (
                ^self , (FilePattern for: string)
              )
          """, """
              (* get the name of directory/file *)
              public %s ^ <FilePattern> = (
                ^self elements last
              )
          """,
      """
              %s: blk <[:FilePattern]> = (
                | elems |
                (self pattern beginsWith: '/') ifTrue: [
                  |i|
                  i:: pattern indexOf: '/' startingAt: 2.
                  elems:: Vector new.
                  elems append: (pattern substringFrom: 1 to: (i - 1)).
                  ((pattern substringFrom: (i+1) to: pattern length) split: separator) do: [ :s |
                    elems append: s.
                  ]
                ] ifFalse: [
                  elems:: self pattern split: separator.
                ].
                elems do: [ :e | blk value: (FilePattern for: e)].
              )
          """,
      """
              %s: els <Vector[FilePattern]> ifFail: fail <[X def]> ^<Int | X> = (
                1 to: els size do:
                  [ :i <Int> |
                    (((els at: i) pattern includes: '*') or: ((els at: i) pattern includes: '?'))
                      ifTrue: [ ^i ]
                  ].
                ^fail value
              )
          """,
      """
              %s: block = (
                block value ifFalse: [
                  self error: 'Assertion Failed'
                ]
              )
          """, """
              %s: block message: msg = (
                block value ifFalse: [
                  self error: ('Assertion Failed: ' + msg)
                ]
              )
          """, """
              public %s ^<Boolean> = (
                ^ vmMirror pathIsAbsolute: self pattern.
              )
          """, """
              (* for each over the matching paths *)
              public %s: blk <[:FilePath]> = (
                | els <Vector[FilePattern]> i <Int> |

                els:: self elements.
                i:: self firstWildElementOf: els
                  ifFail: [ ^self shouldNotHappen ].

                (i = els size) ifTrue: [ self terminalPathsDo: blk ]
                ifFalse: [ | rest <FilePattern> tmp |
                    (i + 1 = els size)
                      ifTrue: [ rest:: els last ] (*optimization*)
                      ifFalse: [
                        tmp:: Vector new.
                        (i + 1) to: (els size) do: [ :j | tmp append: (els at: j)].
                        rest:: FilePattern forAll: tmp.
                      ].

                    tmp:: Vector new.

                    1 to: i do: [ :j | tmp append: (els at: j)].
                    (FilePattern forAll: tmp) pathsDo: [ :fp <FilePath> |
                      fp isDirectory ifTrue: [ (fp, rest) pathsDo: blk ]  ]
                  ]
              )
          """, """
              %s: pat <FilePattern> ^<String> = (
                ^(pat pattern endsWith: separator)
                  ifTrue: [ pat pattern + self pattern ]
                  ifFalse: [ pat pattern + separator + self pattern ]
              )
          """,
      """
              %s: blk <[:FilePath]> = (
                (* assumes that the last element of the receiver is the only one that contains wildcards *)
                | data  hnd  allpat  pat  els <Vector[FilePattern]> prefix paths temp|
                els:: self elements.
                els size = 1
                  ifTrue: [
                    allpat:: FilePattern for: '*.*'.
                    prefix:: FilePattern for: ''.
                  ]
                  ifFalse: [
                    allpat:: self containingDirectory pattern, (separator + '*.*').
                    temp:: Vector new: (els size - 1).
                    1 to: (els size - 1) do: [ :i | temp append: (els at: i) ].
                    prefix:: (FilePattern forAll: temp) pattern concatenate: separator.
                ].
                pat:: els last pattern. (*the part with wildcard i.e. asdf* *)

                (*get filenames in directory*)
                paths:: vmMirror pathContents: prefix.
                paths do: [ :next |
                  ((string: next platformMatch: pat) and: [ next <> '.' and: [ next <> '..' ]])
                    ifTrue: [
                      blk value: (FilePath for: (prefix concatenate: next))
                      ]. (*file matches wildcards*)
                ].
              )
          """,
      """
              %s: s platformMatch: m = (
                ^ string: s match: m si: 1 mi: 1
              )
          """, """
              %s: s match: m si: si mi: mi = (
                |mi_ si_|
                mi_:: mi.
                si_:: si.
                [(mi_ <= m length)] whileTrue: [
                  ((m charAt: mi_) = "?") ifTrue: [
                    (*match single character except empty*)
                    (si_ <= s length) ifFalse: [
                      ^false.
                    ].
                    mi_:: mi_ + 1.
                    si_:: si_ + 1.
                  ] ifFalse: [
                    (*multi match*)
                    ((m charAt: mi_) = "*") ifTrue: [
                      (string: s match: m si: si_ mi: (mi_ + 1)) ifTrue: [
                        ^true.
                      ].
                      (si <= s length) ifTrue: [
                        (string: s match: m si: (si_ + 1) mi: mi_) ifTrue: [
                          ^true.
                        ]
                      ].
                      ^false.
                    ] ifFalse: [
                      (*regular match*)
                      (s charAt: si_) = (m charAt: mi_) ifFalse: [
                        ^false.
                      ].
                      si_:: si_ + 1.
                      mi_:: mi_ + 1.
                    ]
                  ]
                ].
                ^((mi_ > m length) and: (si_ > s length))
              )
          """, """
              public %s ^ <FilePattern> = (
                | e r |
                e:: self elements.
                self assert: [ e size > 1 ] message: ''.
                r:: Vector new: (e size - 1).
                1 to: (e size - 1) do: [ :i |
                  r append: (e at: i)
                ].
                ^ FilePattern forAll: r
              )
          """, """
              %s: dirPath <FilePath> ifFail: fail = (
                self subclassResponsibility
              )
          """,
      """
              public %s = (
                (* This deletes all files and directories that match the specified pattern, with a dynamic failure if
                  unsuccessful. *)
                self deleteIfFail: [ :err <Symbol> | self error: err ]
              )
          """,
      """
              public %s: blk <[:Symbol]> = (
                (* This deletes all files and directories that match the specified pattern.
                  This should be used with extreme caution.  Directories that are
                  not empty are not deleted.  If any matching files or directories cannot
                  be deleted, blk is evaluated with an error symbol *)
                self pathsDo: [ :p <FilePath> |
                  p deletePathIfFail: [ :err <Symbol> |
                    blk value: err.
                    ^self
                  ]
                ]
              )
          """, """
              %s ^ <Vector[FilePattern]> = (
                (* Return a collection of the elements that you would get from elementsDo: *)
                | els |
                els:: Vector new.
                self elementsDo:
                  [ :el <FilePattern> |
                    els append: el.
                  ].
                ^els
              )
          """, """
              public %s: path <FilePath> ^ <FilePattern> = (
                ^FilePattern for: (self patternExtending: path)
              )
          """, """
              public %s: pat <FilePattern> ^ <FilePattern> = (
                ^FilePattern for: (self patternExtending: pat)
              )
          """, """
              public %s ^ <Boolean> = (
                (* Returns true if the receive is a FilePath (i.e. contains no wildcards) *)
                ^false
              )
          """, """
              public %s ^ <Boolean> = (
                ^self isAbsolute not
              )
          """, """
              public %s ^ <Vector[FilePath]> = (
                (* Return a collection of the elements that you would get from pathsDo: *)
                | els |
                els:: Vector new.
                self pathsDo: [ :el <FilePath> |
                  els append: el.
                ].
                ^els
              )
          """, """
              public %s ^ <String> = (
                ^pattern_0
              )
          """, """
              public %s: p <String> = (
                pattern_0:: p
              )
          """, """
               public %s: pat <String>  ^<FilePattern> = (
                ^self new pattern: pat
              )
          """, """
              public %s ^ <Character> = (
                ^"*"
              )
          """, """
              public %s: pat <String> ^<Boolean> = (
                ^(pat includes: multipleMatchWildcardCharacter)
                  or: [ pat includes: singleMatchWildcardCharacter ]
              )
          """, """
              public %s  ^ <Character> = (
                ^"?"
              )
          """, """
              public %s: patternElements <Vector[FilePattern]>  ^ <FilePattern> = (
                | p <FilePattern> |
                patternElements do:
                  [ :el <FilePattern> |
                    p isNil
                      ifTrue: [ p:: el ]
                      ifFalse: [ p:: p, el ]
                  ].
                ^p
              )
          """, """
              public %s: els <Vector[Str]> ^ <FilePattern> = (
                ^self forAll: (els collect: [ :s <String> | FilePattern for: s ])
              )
          """, """
              %s: block = (
                block value ifFalse: [
                  self error: 'Assertion Failed'
                ]
              )
          """, """
              %s: block message: msg = (
                block value ifFalse: [
                  self error: ('Assertion Failed' + msg)
                ]
              )
          """, """
               (* concatenation *)
              public %s: extension <FilePattern> ^ <FilePattern> = (
                ^ extension extendPath: self (* ) *)
              )
          """, """
              public %s: newPath <FilePath> ifFail: fail <[:IOException]> = (
                vmMirror path: (self pattern) copyAs: (newPath pattern) ifFail: fail.
              )
          """,
      """
              (* create directory from this path *)
              public %s = (
                ^ createDirectoryIfFail: [:s | IOException signalWith: 'Could not create directory: ' + self name ]
              )
          """,
      """
              (* create directory from this path *)
              public %s: fail <[:Symbol]> = (
                vmMirror pathCreateDirectory: self pattern ifFail: fail.
              )
          """,
      """
              (* delete directory named by this path *)
              public %s = (
                ^ deleteDirectoryIfFail: [:s | IOException signalWith: 'Could not delete directory: ' + self name ]
              )
          """,
      """
              (* delete directory named by this path *)
              public %s: fail <[:Symbol]> = (
                vmMirror pathDeleteFileDir: self pattern ifFail: fail.
              )
          """,
      """
              (* delete file named by this path *)
              public %s = (
                ^ deleteFileIfFail: [:s | IOException signalWith: 'Could not delete file: ' + self name ]
              )
          """,
      """
              (* delete file named by this path *)
              public %s: fail <[:Symbol]> = (
                vmMirror pathDeleteFileDir: self pattern ifFail: fail.
              )
          """, """
              public %s: fail <[:Symbol]> = (
                self exists
                  ifFalse: [  blk value: #NoSuchFileOrDirectory.
                          ^self   ].
                self isDirectory
                  ifTrue: [ self deleteDirectoryIfFail: fail ]
                  ifFalse: [ self deleteFileIfFail: fail ]
              )
          """, """
              public %s ^ <Boolean> = (
                ^ vmMirror pathFileExists: self pattern.
              )
          """, """
              public %s ^ <Boolean> = (
                ^ vmMirror pathIsDirectory: self pattern.
              )
          """, """
              public %s ^ <Boolean> = (
                ^ vmMirror pathIsReadOnly: self pattern.
              )
          """, """
              public %s = (
                ^ vmMirror pathLastModified: self pattern.
              )
          """, """
              public %s: newPath <FilePath> ifFail: fail <[:IOException]> = (
                vmMirror path: self pattern moveAs: newPath pattern ifFail: fail.
              )
          """, """
              public %s = (
                ^ vmMirror pathGetSize: self pattern.
              )
          """, """
              public %s: newPath <FilePath> = (
                self copyAs: newPath ifFail: [ :err <Symbol> |
                  self error: err
                ]
              )
          """, """
              (* Create a copy in the same directory with the specified name.
                 The new path is returned. *)
              public %s: name <String> ^ <FilePath> = (
                ^ self copyNamed: name ifFail: [ :err <Symbol> | self error: err ]
              )
          """, """
              (* Create a copy in the same directory with the specified name.
                 The new path is returned. *)
              public %s: name <String> ifFail: fail <[:IOException]>  ^ <FilePath> = (
                | newPath <FilePath> |
                newPath:: self containingDirectory, (FilePath for: name).
                self copyAs: newPath ifFail: [ :err <Symbol> |
                  self error: err
                ].
                ^ newPath
              )
          """, """
              public %s ^ <Vector[FilePath]> = (
                (* A file path's elements must also be filepaths *)
                ^ super elements
              )
          """, """
              (* returns a pattern that represents all subfiles of a directory *)
              public %s ^ <Vector[FilePath]>  = (
                ^ (self, (FilePattern for: '*')) paths
              )
          """, """
              public %s: path <FilePath> ^ <FilePath> = (
                ^ FilePath for: (self patternExtending: path)
              )
          """, """
              public %s ^ <Boolean> = (
                ^ true
              )
          """, """
              public %s ^ <Boolean> = (
                ^ self isReadOnly not
              )
          """, """
              public %s: newPath <FilePath> = (
                self moveAs: newPath
                  ifFail: [ :err <Symbol> | self error: err ]
              )
          """, """
              public %s ^ <String> = (
                ^ self pattern
              )
          """,
      """
              (* Open file with given access mode.
                 Access mode can be #read, #write, or #readWrite. *)
              public %s: mode <Symbol> ifFail: errBlock <[:Symbol | X def]> ^ <FileDescriptor | X> = (
                ^ (FileDescriptor for: self pattern mode: mode)
                    openIfFail: errBlock
              )
          """,
      """
              (* Open file with given access mode.
                 Access mode can be #read, #write, or #readWrite. *)
              public %s: mode <Symbol> ^ <FileDescriptor | X> = (
                ^ open: mode ifFail: [:s |
                    s = #FileNotFound ifTrue: [
                      FileNotFoundException signalFor: name with: 'File not found' ].

                    s = #InvalidAccessMode ifTrue: [
                      IOException signalWith: 'Invalid access mode (' + mode + ') for file ' + name asString ].

                    IOException signalWith: 'Failed to open file ' + name asString ]
              )
          """,
      """
              public %s: blk <[:FilePath]> = (
                self exists ifTrue: [  blk value: self ]
              )
          """,
      """
              public %s: p <String> = (
                super pattern: p.
                self assert: [ (FilePattern patternHasWildcards: p) not ] message: 'shouldnothavewildcards'.
              )
          """,
      """
              (* Rename in the same directory. The new path is returned. *)
              public %s: name <String> ^ <FilePath> = (
                ^ self rename: name ifFail: [ :err <Symbol> | self error: err ]
              )
          """, """
              (* Rename in the same directory. The new path is returned. *)
              public %s: name <String> ifFail: fail <[:IOException]> ^ <FilePath> = (
                | newPath <FilePath> |
                newPath:: self containingDirectory, (FilePath for: name).
                self moveAs: newPath ifFail: fail.
                ^ newPath
              )
          """, """
              public %s ^ <String> = (
                ^ elements last pattern
              )
          """, """
              public %s ^ <CharInputStream> = (
                ^CharacterReadConverter on: self readStream.
              )
          """, """
              public %s ^ <CharOutputStream> = (
                ^CharacterInputOutputConverter on: self writeStream
              )
          """, """
              public %s ^ <ExternalReadStream> = (
                ^ExternalReadStream onDescriptor: (self open: #read)
              )
          """, """
              public %s ^ <ExternalReadWriteStream> = (
                ^ExternalReadWriteStream onDescriptor: (self open: #readWrite)
              )
          """, """
          public %s ^ <ExternalReadWriteStream> = (
            ^ExternalOutputStream onDescriptor: (self open: #write)
          )
                """
  };
}
