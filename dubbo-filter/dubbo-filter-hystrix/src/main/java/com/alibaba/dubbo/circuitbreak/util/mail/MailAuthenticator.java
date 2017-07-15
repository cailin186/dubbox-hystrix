package com.alibaba.dubbo.circuitbreak.util.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator
{

	public PasswordAuthentication getPasswordAuthentication( String username, String password )
	{
		PasswordAuthentication pa = null;
		pa = new PasswordAuthentication( username, password );
		return pa;
	}
}
