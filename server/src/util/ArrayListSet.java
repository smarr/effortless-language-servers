package util;

import java.util.ArrayList;
import java.util.Collection;


/**
 * This is an ArrayList that does not allow duplicate items to be added.
 *
 * <p>The assumption is that the produce data is usually in program, i.e., lexical
 * order. And, any kind of duplication, because of implicit language constructs,
 * can be easily avoided by checking whether the last added element is
 * the same as the next one to be added.
 *
 * <p>Though, we also check all other elements, to be sure not to have duplicates.
 * This is needed since we may combine lists from different files.
 */
public class ArrayListSet<E> extends ArrayList<E> {

  private static final long serialVersionUID = 7471454862969758905L;

  public ArrayListSet() {
    super();
  }

  public ArrayListSet(final int initialCapacity) {
    super(initialCapacity);
  }

  public ArrayListSet(final Collection<E> c) {
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
      for (int i = 0; i < size - 1; i += 1) {
        if (get(i).equals(e)) {
          return false;
        }
      }
    }
    return super.add(e);
  }
}
