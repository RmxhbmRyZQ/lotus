package cn.flandre.lotus.exception;

public class UnsupportedDatabaseException extends RuntimeException {
    public UnsupportedDatabaseException(){
        super();
    }

    public UnsupportedDatabaseException(String message){
        super(message);
    }
}
