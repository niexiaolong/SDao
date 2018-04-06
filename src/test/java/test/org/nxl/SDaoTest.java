package test.org.nxl;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.Before;
import org.junit.Test;
import org.nxl.dao.SDao;
import org.nxl.model.Author;

import java.util.List;

public class SDaoTest {

    private SDao dao;

    @Before
    public void init(){
        DruidDataSource ds = new DruidDataSource();
        ds.setUsername("root");
        ds.setPassword("long603");
        ds.setUrl("jdbc:mysql://127.0.0.1:3306/cug");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        dao = new SDao(ds);
        dao.setSqlMapper("org.nxl.model");
    }

    @Test
    public void uniqueOneTest(){
        String sql = "select * from author limit 1";
        Author author = dao.uniqueOne(sql, Author.class);
        System.out.println(author);

        String sql2 = "select name from author limit 1";
        String name = dao.uniqueOne(sql2, String.class);
        System.out.println(name);
    }

    @Test
    public void firstTest(){
        String sql = "select * from author";
        Author author = dao.first(sql, Author.class);
        System.out.println(author.getName());
    }

    @Test
    public void listTest(){
        String sql = "select * from author";
        List<Author> authors = dao.list(sql, Author.class);
        for(Author author : authors){
            System.out.println(author);
        }

        String sql2 = "select age from author";
        List<Integer> ages = dao.list(sql2, Integer.class);
        for(Integer age : ages){
            System.out.println(age);
        }
    }

    @Test
    public void saveTest(){
        Author author = new Author();
        author.setAge(79);
        author.setName("聂晓龙");
        author.setCreatetime(System.currentTimeMillis()/1000);
        try {
            dao.save(author);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void updateTest(){
        String sql = "select * from author where authorid = 1";
        Author author = dao.first(sql, Author.class);
        author.setName("霍金-升级版");
        try {
            dao.update(author);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void deleteTest(){
        String sql = "select * from author where authorid = 5";
        Author author = dao.first(sql, Author.class);
        try {
            dao.delete(author);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}