package com.iandna.gateway.hikVision;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.sf.json.JSONObject;

@RestController
@RequestMapping("/gateway/hikVision")
public class HikVisionController {
	private static final Logger logger = LoggerFactory.getLogger(HikVisionController.class);
	@Autowired
	HikvisionCommunicator hkComm;
	
	@GetMapping("/test.json")
	public JSONObject getUrl() {
		JSONObject result = new JSONObject();
		result.put("abc", "abc");
		result.put("def", "def");
		result.put("ghj", "ghj");
		return result;
	}

	@GetMapping("/getToken.json")
	public JSONObject getAccessToken() {
		JSONObject result = null;
		try {
			result = hkComm.getAcsToken();
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@PostMapping("/subAccount.json")
	public JSONObject createSubAccount(@RequestBody HikVisionVo param) {
		JSONObject result = null;
		try {
			param.toString();
//			result = hkComm.getAcsToken();
			logger.debug(param.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
	
	@GetMapping("/subAccountToken.json")
	public JSONObject getSubAccountToken(@RequestBody HikVisionVo param) {
		JSONObject result = null;
		try {
			param.toString();
			result = hkComm.getSubAcsToken(param.getAccountId());
			logger.debug(param.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
