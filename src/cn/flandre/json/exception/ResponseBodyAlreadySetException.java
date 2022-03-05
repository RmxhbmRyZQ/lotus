package cn.flandre.json.exception;

public class ResponseBodyAlreadySetException extends RuntimeException {
    public ResponseBodyAlreadySetException(){
        super();
    }

    public ResponseBodyAlreadySetException(String message){
        super(message);
    }
}
