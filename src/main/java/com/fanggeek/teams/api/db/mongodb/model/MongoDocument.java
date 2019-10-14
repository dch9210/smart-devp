package com.fanggeek.teams.api.db.mongodb.model;

import java.io.Serializable;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class MongoDocument implements Serializable {

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 7770299154208711024L;

	@Id
	private String _id;

	@Indexed
	protected Date modifyTime;
	@Indexed
	protected Date createTime;

	/**
	 * <br>
	 * 1为删除，其他值为 未删除
	 */
	@Indexed
	protected Integer del_flag_inner;

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public Date getModifyTime() {
		return modifyTime;
	}

	public void setModifyTime(Date modifyTime) {
		this.modifyTime = modifyTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getDel_flag_inner() {
		return del_flag_inner;
	}

	public void setDel_flag_inner(Integer del_flag_inner) {
		this.del_flag_inner = del_flag_inner;
	}

	@Override
	public String toString() {
		return "MongoDocument [_id=" + _id + ", modifyTime=" + modifyTime + ", createTime=" + createTime
				+ ", del_flag_inner=" + del_flag_inner + "]";
	}

}
