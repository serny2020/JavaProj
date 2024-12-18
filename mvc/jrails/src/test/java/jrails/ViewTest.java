package jrails;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.Assert.*;

public class ViewTest {

    @Test
    public void empty() {
        assertThat(View.empty().toString(), isEmptyString());
    }

    @Test
    public void testT() {
        Html html = new Html().t("text");
        assertThat(html.toString(), is("text"));
    }

    @Test
    public void testP() {
        Html html = new Html().p(new Html("child"));
        assertThat(html.toString(), is("<p>child</p>"));
    }
    @Test
    public void p() {
        assertThat(View.p(new Html("child")).toString(), is("<p>child</p>"));
    }

    @Test
    public void form() {
        assertThat(View.form("action", new Html("child")).toString(), is("<form action=\"action\" accept-charset=\"UTF-8\" method=\"post\">child</form>"));
    }

    @Test
    public void bookView() {
        assertEquals(View.p(View.strong(View.t("Title:")).t("book")).
                        p(View.strong(View.t("Author:")).t("author")).
                        p(View.strong(View.t("Copies:")).t("0")).
                        t(View.link_to("Edit", "/edit?id=1")).t(" | ").
                        t(View.link_to("Back", "/")).toString(),
                "<p><strong>Title:</strong>book</p>" +
                        "<p><strong>Author:</strong>author</p>" +
                        "<p><strong>Copies:</strong>0</p>" +
                        "<a href=\"/edit?id=1\">Edit</a> | <a href=\"/\">Back</a>");
    }

    @Test
    public void submit() {
        Html html = new Html();
        assertEquals(html.submit("Save").toString(), "<input type=\"submit\" value=\"Save\"/>");
    }

    @Test
    public void seqDivH1() {
        Html h = new Html().div(View.t("text"));
        Html preH = new Html().h1(View.t("h1"));
        assertEquals(preH.seq(h).toString(), "<h1>h1</h1><div>text</div>");
    }

    @Test
    public void testEmpty() {
        Html result = View.empty();
        assertEquals("", result.toString());
    }

    @Test
    public void testBr() {
        Html result = View.br();
        assertEquals(result.toString(), "<br/>");
    }

//separate classes above and below

    @Test
    public void testToString() {
        Html html =new Html().t("toString Test...");
        assertEquals(html.toString(), "toString Test...");
    }

    @Test
    public void testP2() {
        Html child = new Html().t("this text is a new paragraph");
        Html parent = new Html().t("").p(child);
        assertEquals(parent.toString(), "<p>this text is a new paragraph</p>");
    }

    @Test
    public void testSubmit() {
        Html sub = View.submit("test");
        assertEquals(sub.toString(), "<input type=\"submit\" value=\"test\"/>");

    }


    @Test
    public void testLink() {
        Html link = View.link_to("Test", "/test");
        assertEquals(link.toString(), "<a href=\"/test\">Test</a>");
    }

}