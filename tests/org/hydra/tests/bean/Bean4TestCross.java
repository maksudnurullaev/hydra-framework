package org.hydra.tests.bean;

import java.util.Set;

import org.hydra.utils.Result;

public class Bean4TestCross {
	private String name = null;
	private Set<Bean4TestCross> links = null;
	
	public void setLinks(Set<Bean4TestCross> inLinks){
			links  = inLinks;
	}
		
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}

	public boolean containsLink(String name) {
		if(links == null) return false;
		
		for(Bean4TestCross bean:links)
			if(bean.getName().equals(name)) return true;
		
		return false;
	}

	public Result getLink(String name) {
		Result result = new Result();
		if(links == null){
			result.setResult(false);
			result.setResult("Links is NULL!");
		}
		
		for(Bean4TestCross bean:links)
			if(bean.getName().equals(name)){
				result.setResult(true);
				result.setObject(bean);
				return result;
			}
		
		result.setResult(false);
		result.setResult("Link not found!");
		return result;
	}
	
}
