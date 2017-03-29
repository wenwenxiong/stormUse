package stormUse.stormUse;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class MessageScheme implements Scheme {
	
	 public List<Object> deserialize(ByteBuffer ser) 
	 {
	     
		 Charset charset = null;  
	     CharsetDecoder decoder = null;  
	     CharBuffer charBuffer = null;  
		 
		 try 
		 {
			 charset = Charset.forName("UTF-8");  
	         decoder = charset.newDecoder();  
	         charBuffer = decoder.decode(ser.asReadOnlyBuffer());  
	         String msg = charBuffer.toString(); 
	         return new Values(msg);
	        
		 } catch (CharacterCodingException e) 
		 {  
	         
	      }
	        return null;
	 }
	 
	 public Fields getOutputFields() {
	        return new Fields("msg");  
	    }

}
