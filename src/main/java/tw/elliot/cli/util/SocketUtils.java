package tw.elliot.cli.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class SocketUtils {
	private static Pattern IPPORT = Pattern.compile("^((([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])):([0-9]+)$");
	public static boolean checkSocketAvailable(String ipport) {
		boolean isAlive = false;

		Matcher matcher = IPPORT.matcher(ipport);

		if (matcher.matches()) {
			String ip = matcher.group(1);
			int port = Integer.parseInt(matcher.group(5));
			log.debug("Found IP[{}] And Port[{}]", ip, port);

			isAlive = checkSocketAvailable(ip, port);
		}

		return isAlive;
	}
	public static boolean checkSocketAvailable(String ip, int port) {
		boolean isAlive = false;
		SocketAddress sa = new InetSocketAddress(ip, port);
		Socket socket = new Socket();
		log.debug("Try to connect IP[{}] And Port[{}]", ip, port);
		try {
			socket.connect(sa, 3000);
			socket.close();

			isAlive = true;
		} catch (IOException e) {
			log.error("Connect [{}:{}] Failed, cause [{}] ",ip, port, e.getMessage());
		}
		return isAlive;
	}
}
