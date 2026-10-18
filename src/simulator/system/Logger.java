package simulator.system;

public class Logger {

    public static void info(String message){
        System.out.println(Ansi.BLUE+"[INFO]: "+Ansi.WHITE+message);
    }

    public static void warn(String message){
        System.out.println(Ansi.YELLOW+"[INFO]: "+Ansi.WHITE+message);
    }

    public static void err(String message){
        System.out.println(Ansi.RED+"[INFO]: "+Ansi.WHITE+message);
    }

    public static void succes(String message){
        System.out.println(Ansi.GREEN+"[INFO]: "+Ansi.WHITE+message);
    }

}
