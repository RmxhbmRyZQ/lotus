package cn.flandre.json.exception;

import java.io.IOException;

public class InvalidHttpHeaderException extends IOException {
    public InvalidHttpHeaderException(){
        super();
    }

    public InvalidHttpHeaderException(String message){
        super(message);
    }
}
