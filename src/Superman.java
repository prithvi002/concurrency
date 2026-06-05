public class Superman {
  //singleton
  //construction is private
  
  //eager initialization
  // private final static Superman superman = new Superman();
  // private Superman(){

  // }

  // public static Superman getInstance()
  // {
  //   return superman;
  // }

  private Superman()
  {

  }

  private static volatile Superman superman;

  public static Superman getInstance()
  {
    if(superman == null)
    {
      synchronized (Superman.class) {
        if(superman == null)
        {
          superman = new Superman();
        }
      } 

    }

    return superman;
  }

  public void fly()
  {
    System.out.println("I am a Superman & I can fly");
  }
}
