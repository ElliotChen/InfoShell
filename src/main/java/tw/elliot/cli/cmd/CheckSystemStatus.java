package tw.elliot.cli.cmd;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import tw.elliot.cli.props.Network;
import tw.elliot.cli.props.Service;
import tw.elliot.cli.util.SocketUtils;

import javax.sql.DataSource;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.function.Consumer;

@ShellComponent
@Slf4j
public class CheckSystemStatus {

	@Autowired
	private Environment env;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private Network network;

	@Autowired
	private Service service;

	@ShellMethod("Check All Status")
	public void all() {
		log.info("Start to check system.");
		log.info("----------------------------------");
		info();
		log.info("----------------------------------");
		network();
		log.info("----------------------------------");
		database();
		log.info("----------------------------------");
		services();
		log.info("----------------------------------");
		log.info("End of check system.");
	}

	@ShellMethod("Show Environment Info")
	public void info() {
		String[] profiles = env.getActiveProfiles();
		log.info("Current Active Profiles: [{}]", profiles);
	}

	@ShellMethod("Check DB Status")
	public void database() {
		log.info("Start to check Database.");
		log.info("Step 1. Check connection by executing 'SELECT 1'");
		jdbcTemplate.query("SELECT 1", new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet resultSet) throws SQLException {
				log.info("Execute SELECT 1 And Result is [" + resultSet.getObject(1)+"]");
				log.info("DB connection is available.");
			}
		});
		try {
			Properties clientInfo = this.jdbcTemplate.getDataSource().getConnection().getClientInfo();
			clientInfo.forEach((k, v) -> log.info(k + ":" + v));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		log.info("End of check Database");
	}

	@ShellMethod("Check Network Status")
	public void network() {
		log.info("Start to check Network");

		for (String socket : network.getSockets()) {
			log.info("Check ip:port ["+socket+"]");
			boolean alive = SocketUtils.checkSocketAvailable(socket);
			if (alive) {
				log.info("[" + socket + "] is available.");
			} else {
				log.info("[" + socket + "] isn't available.");
			}
		}

		log.info("End of check Network");
	}

	@ShellMethod("Check Service/API(Restful) Status")
	public void services() {
		log.info("Start to check Service/API");

		for (String surl : service.getRestfuls()) {
			log.info("Check Service URL ["+surl+"]");
			try {
				URL url = new URL(surl);
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.setRequestMethod("GET");
				con.connect();
				int code = con.getResponseCode();
				log.info("[{}] return code [{}]",surl, code);
			} catch (Exception e) {
				log.info("[{}] connect failed, cause [{}]",surl, e.getMessage());
			}
		}
		log.info("End of check Service/API");
	}
}
