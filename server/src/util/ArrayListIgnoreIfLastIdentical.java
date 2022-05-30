package util;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This is a trivial optimization to skip redundant data in results.
 *
 * The assumption is that the produce data is usually in program, i.e., lexical
 * order. And, any kind of duplication, because of implicit language constructs,
 * can be easily avoided by checking whether the last added element is
 * the same as the next one to be added.
 */
public class ArrayListIgnoreIfLastIdentical<E> extends ArrayList<E> {

  private static final long serialVersionUID = 7471454862969758905L;

  public ArrayListIgnoreIfLastIdentical() {
    super();
  }

  public ArrayListIgnoreIfLastIdentical(final int initialCapacity) {
    super(initialCapacity);
  }

  public ArrayListIgnoreIfLastIdentical(final Collection<E> c) {
    super(c);
  }

  @Override
  public boolean add(final E e) {
    int size = size();
    if (size > 0) {
      E last = get(size - 1);
      if (last.equals(e)) {
        return false;
      }
    }
    return super.add(e);
  }
}
