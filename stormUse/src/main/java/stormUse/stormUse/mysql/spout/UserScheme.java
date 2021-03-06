package stormUse.stormUse.mysql.spout;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.List;

import org.apache.storm.spout.Scheme;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

public class UserScheme implements Scheme {
	
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
	         //String regex = "\\s+";
	         String[] list = msg.split(",");
	         Integer user_id = Integer.valueOf(list[0]);
	         
	         return new Values(user_id , list[1] , System.currentTimeMillis());
	        
		 } catch (CharacterCodingException e) 
		 {  
	         
	      }
	        return null;
	 }
	 
	 public Fields getOutputFields() {
	       //return new Fields("msg");  
	       return new Fields("user_id","user_name","create_date");
	    }

}
