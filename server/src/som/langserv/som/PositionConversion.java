package som.langserv.som;

import org.eclipse.lsp4j.Location;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;

import bdt.source.SourceCoordinate;


public class PositionConversion {

  private PositionConversion() {}

  public static Range toRange(final Source source, final long coordWithLength) {
    int line = SourceCoordinate.getLine(source, coordWithLength);
    int column = SourceCoordinate.getColumn(source, coordWithLength);
    int length = SourceCoordinate.getLength(coordWithLength);

    return toRange(line, column, length);
  }

  public static Range toRange(final Source source, final long coord, final int length) {
    int line = SourceCoordinate.getLine(source, coord);
    int column = SourceCoordinate.getColumn(source, coord);
    return toRange(line, column, length);
  }

  public static Range toRange(final Source source, final int coord, final int length) {
    int line = SourceCoordinate.getLine(source, coord);
    int column = SourceCoordinate.getColumn(source, coord);
    return toRange(line, column, length);
  }

  public static Position pos(final int startLine, final int startChar) {
    return new Position(startLine - 1, startChar - 1);
  }

  public static Range toRange(final SourceSection ss) {
    Range range = new Range();
    range.setStart(pos(ss.getStartLine(), ss.getStartColumn()));

    range.setEnd(pos(ss.getEndLine(), ss.getEndColumn()));
    return range;
  }

  public static Range toRange(final int line, final int col, final int length) {
    Range range = new Range();
    range.setStart(pos(line, col));
    range.setEnd(pos(line, col + length));
    return range;
  }

  public static Range toRangeMax(final int startLine, final int startColumn) {
    Range range = new Range();
    range.setStart(pos(startLine, startColumn));
    range.setEnd(pos(startLine, Integer.MAX_VALUE));
    return range;
  }

  public static Location getLocation(final Source source, final long coord) {
    Location loc = new Location();
    loc.setUri(source.getURI().toString());
    loc.setRange(toRange(source, coord));
    return loc;
  }

  public static Location getLocation(final SourceSection ss) {
    Location loc = new Location();
    loc.setUri(ss.getSource().getURI().toString());
    loc.setRange(toRange(ss));
    return loc;
  }

  public static Position getStart(final Source source, final int coord) {
    int line = SourceCoordinate.getLine(source, coord);
    int column = SourceCoordinate.getColumn(source, coord);

    return new Position(line - 1, column - 1);
  }

  public static Position getEnd(final Source source, final int coord, final int length) {
    int line = SourceCoordinate.getLine(source, coord);
    int column = SourceCoordinate.getColumn(source, coord);

    return new Position(line - 1, column + length - 1);
  }
}
