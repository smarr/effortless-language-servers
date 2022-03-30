

class example extends Benchmark {
  public static void main(final String[] args) {
    house houseA = new house();
    houseA.numberOfWindows = 3;
    System.out.println(args);
  }

  public int newHome(final int windows, final int doors) {
    house newHouse = new house();
    int cost = windows * doors;
    newHouse.numberOfWindows = windows;
    newHouse.numberOfdoors = doors;
    
    return cost;
  }

  @Override
  public Object benchmark() {
    house houseA = new house();
    houseA.numberOfWindows = 3;
    houseA.numberOfdoors = 6;

    return newHome(houseA.numberOfWindows, houseA.numberOfdoors);
  }

  @Override
  public boolean verifyResult(final Object result) {

    return 18 == (int) result;
  }

}

class house {
  public int numberOfdoors;
  public int numberOfWindows;
  int        numberofwindows;

  private void nameofhouse() {

  }
}
