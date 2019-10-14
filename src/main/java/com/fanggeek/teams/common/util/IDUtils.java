package com.fanggeek.teams.common.util;

import java.util.UUID;

public class IDUtils {

	public static String genUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

}
