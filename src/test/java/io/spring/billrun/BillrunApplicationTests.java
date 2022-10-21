package io.spring.billrun;

import io.spring.billrun.model.Bill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import static org.assertj.core.api.Assertions.assertThat;
import javax.sql.DataSource;
import java.util.List;

@SpringBootTest
class BillrunApplicationTests {

	@Autowired
	DataSource dataSource;
	/* to execute a query and retrieve the results of the billrun */
	JdbcTemplate jdbcTemplate;

	@BeforeEach
	public void setup(){
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
//	@Test
//	void contextLoads() {
//	}
	@Test
	public void testJobResults(){
		List<Bill> billList = this.jdbcTemplate.query("select id, " +
						"first_name, last_name, minutes, data_usage, bill_amount " +
						"FROM bill_statements ORDER BY id",
				(rs, rowNum) -> new Bill(rs.getLong("id"),
						rs.getString("FIRST_NAME"), rs.getString("LAST_NAME"),
						rs.getLong("DATA_USAGE"), rs.getLong("MINUTES"),
						rs.getDouble("bill_amount")));

//		assertThat(billList.size()).isEqualTo(36);
		Bill bill = billList.get(0);
		assertThat(bill.getBillAmount()).isEqualTo(6.0);
		assertThat(bill.getFirstName()).isEqualTo("jane");
		assertThat(bill.getLastName()).isEqualTo("doe");
		assertThat(bill.getId()).isEqualTo(1);
		assertThat(bill.getMinutes()).isEqualTo(500);
		assertThat(bill.getDataUsage()).isEqualTo(1000);
	}

}
