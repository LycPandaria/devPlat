package edu.devplat.sys.model;

import java.io.Serializable;

/**
 * β����Ʒ����Entity
 * @author Liyingcong
 * @version 2018-12-03
 */
public class TrunkItem implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String trunkType;		// β������
	private String itemCode;		// ��Ʒ����
	private String itemName;		// ��Ʒ����
	
	public TrunkItem() {
	}

	public String getTrunkType() {
		return trunkType;
	}

	public void setTrunkType(String trunkType) {
		this.trunkType = trunkType;
	}
	
	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itmeCode) {
		this.itemCode = itmeCode;
	}
	
	public String getItemName() {
		return itemName;
	}

	public void setItemName(String itemName) {
		this.itemName = itemName;
	}
}