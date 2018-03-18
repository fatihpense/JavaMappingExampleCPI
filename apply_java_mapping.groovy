import com.sap.gateway.ip.core.customdev.util.Message;
import com.medepia.pi.education.simple1.SimpleJavaMappingCPI;


def Message processData(Message message) {
	
	//Body as string
	def body = message.getBody(String.class);
	
	def javaMapping = new SimpleJavaMappingCPI();
	def result = javaMapping.processString(body);
	message.setBody(result);
	
	return message;
}

