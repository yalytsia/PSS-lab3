import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicReference;

public class Atomic {

    public static void main(String[] args) throws Exception {
        DB db = new DB();
        AtomicReference<Connection> ar = new AtomicReference<Connection>();
        ar.set(db.getConnection());

        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(8);
        
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 10; i++) {
            executor.execute(new Job(ar));
        }
        executor.shutdown();
        
        while (!executor.isTerminated());
        long endTime = System.currentTimeMillis();
        Connection conn = ar.get();
            if (conn != null)
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        
        System.out.printf("\nDONE = %d", endTime - startTime);
    }


    
    private static class Job implements Runnable {
		private AtomicReference<Connection> ar;

		public Job(AtomicReference<Connection> ar) {
            this.ar = ar;
		}

		@Override
        public void run() {
            Connection conn = null;
            try {
                conn = this.ar.get();
                Statement st = conn.createStatement();
            
                try {
                    st.executeUpdate("INSERT INTO parrots (name, color, age) "
                        +"VALUES ('Crag', 'red', 92)");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } 
            
        }
	}

}




