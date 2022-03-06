package cn.flandre.lotus.exception;

public class ResponseBodyAlreadySetException extends RuntimeException {
    public ResponseBodyAlreadySetException(){
        super();
    }

    public ResponseBodyAlreadySetException(String message){
        super(message);
    }
}
