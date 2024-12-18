package jrails;

import books.BookController;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

public class JRouterTest {

    private JRouter jRouter;

    @Before
    public void setUp() throws Exception {
        jRouter = new JRouter();
    }

    @Test
    public void addRoute() {
        jRouter.addRoute("GET", "/", String.class, "index");
        assertThat(jRouter.getRoute("GET", "/"), is("java.lang.String#index"));
    }

    @Test
    public void addRouteTest1() {
        try {
            jRouter.addRoute("", "/", String.class, "");
        } catch (Exception e) {
            System.err.println("FAILURE EXPECTED: " + e);
        }
    }
    @Test
    public void addRouteTest2() {
        try {
            jRouter.addRoute("123", "/", Integer.class, "#");
        } catch (Exception e) {
            System.err.println("FAILURE EXPECTED: " + e);
        }
    }

    @Test
    public void getRouteNonExistent() {
        assertThat(jRouter.getRoute("GET", "/nonexistent"), is(nullValue()));
    }
    @Test
    public void addRouteTest3() {
        try
        {
            jRouter.addRoute("GET", "/", String.class, "index");
            jRouter.addRoute("GET", "/", Integer.class, "index");
        }
        catch (Exception e)
        {
            System.err.println("FAILURE EXPECTED: " + e);
        }
    }


    @Test
    public void testBookRoute(){
        JRouter r = new JRouter();
        r.addRoute("GET", "/", BookController.class, "index");
        r.addRoute("GET", "/show", BookController.class, "show");
        r.addRoute("GET", "/new", BookController.class, "new_book");
        r.addRoute("GET", "/edit", BookController.class, "edit");
        r.addRoute("POST", "/create", BookController.class, "create");
        r.addRoute("POST", "/update", BookController.class, "update");
        r.addRoute("GET", "/destroy", BookController.class, "destroy");

        r.route("GET", "/",new HashMap<String, String>());
        r.route("GET", "/new",new HashMap<String, String>());
    }

}