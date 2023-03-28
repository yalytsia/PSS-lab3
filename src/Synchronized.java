import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Synchronized {
    
    public static void main(String[] args) throws Exception {
        DB db = new DB();
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);

        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            executor.execute(new Job(db));
        }
        executor.shutdown();

        while (!executor.isTerminated());
        long endTime = System.currentTimeMillis();
        System.out.printf("\nDONE = %d", endTime - startTime);

        
    }


    
    private static class Job implements Runnable {
		private DB db;

		public Job(DB db) {
			this.db = db;
		}

		@Override
        public synchronized void run() {
            try {
                Connection conn = this.db.getConnection();
                Statement st = conn.createStatement();
                st.executeUpdate("INSERT INTO parrots (name, color, age) "
                +"VALUES ('Fred', 'blue', 66)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            
        }
	}

}




