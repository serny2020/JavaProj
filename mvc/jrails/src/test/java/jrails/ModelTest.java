package jrails;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import books.Book;

import java.io.File;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

public class ModelTest {

    private Model model;

    @Before
    public void setUp() throws Exception {
        model = new Model(){};
    }

    @Test
    public void testWithNullField() {
        Model.reset();
        Book book = new Book();
        book.title = "The Cat and the Hat";
        book.author = "Dr. Seuss";
        book.num_copies = 100;

        book.save();
//
        book.author = "";

        book.save();
        assert(book.id() > 0);
        Book checkBook = Model.find(Book.class, book.id());
        assert(checkBook.title.equals("The Cat and the Hat"));
        assertNotNull(checkBook);
        assertEquals("The Cat and the Hat", checkBook.title);
        assertEquals(checkBook.author, "");
        assertEquals(100, checkBook.num_copies);
    }

    @Test
    public void test1() {
        Model.reset();
        Book book1 = new Book();
        Book book2 = new Book();
        Book book3 = new Book();

        assertThat(book1.id(), notNullValue());
        book1.title = "ABC";
        book1.author = "123";
        book1.num_copies = 1;
        assert(book1.id() == 0);
        book1.save();
        assert(book1.id() > 0);

        book2.title = "Hitchhiker's Guide to the Galaxy";
        book2.author = "Douglas Adams";
        book2.num_copies = 42;

        book2.save();
        book2.title = "Restaurant at the End of the Universe";
        book2.save();
        assert(book2.title.equals("Restaurant at the End of the Universe"));

        book3.title = "Find Me";
        book3.author = "Author";
        book3.num_copies = 1;

        book3.save();
        assert(book3.id() > 0);
        Book found = Model.find(Book.class, book3.id());
        assert(found.title.equals("Find Me"));

        List<Book> books = Model.all(Book.class);
        assert(books.size() == 3);
        Book book = books.get(0);
        assert(book.title.equals("ABC"));
        }


    @Test
    public void roundTrip() {
        TestModel m = new TestModel();
        m.s = "foo";
        m.i = 123;
        m.b = false;

        assertThat(m.id(), is(0));

        m.save();

        int id = m.id();
        assertThat(m.id(), not(is(0)));

        TestModel found = Model.find(TestModel.class, id);

        assertThat(found.id(), is(m.id()));
        assertThat(found.s, is("foo"));
        assertThat(found.i, is(123));
        assertThat(found.b, is(false));
    }

    @Test
    public void test() {
        Model.reset();
        Book book = new Book();
        book.title = "Software Engineering";
        book.author = "Software Engineer";
        book.num_copies = 4;
        book.save();

        Book wantedBook = Model.find(Book.class, book.id());

        assertEquals(wantedBook.title, book.title);
        assertEquals(wantedBook.author, book.author);
        assertEquals(wantedBook.num_copies, book.num_copies);

        Book newBook = new Book();
        newBook.title = book.title;
        newBook.author = book.author;
        newBook.num_copies = book.num_copies + 1;
        book.num_copies = newBook.num_copies;

        assertEquals(newBook.title, book.title);
        assertEquals(newBook.author, book.author);
        assertEquals(newBook.num_copies, book.num_copies);
    }
    @Test
    public void id() {
        assertThat(model.id(), notNullValue());
        assertThat((new Model()).id(), notNullValue());
        assertThat( (new Book()).id(), notNullValue());
    }

    @Test
    public void bookTest()  throws IllegalAccessException{
        Model.reset();

        Book b = new Book();
        b.title = "Programming Languages: Build, Prove, and Compare";
        b.author = "Norman Ramsey";
        b.num_copies = 234;
        // The book b exists in memory but isn't saved to the db
        System.out.println(b.id());
        b.save(); // now the book is in the db
        b.num_copies = 42; // the book in the db still has 234 copies
        b.save(); // now the book in the db has 42 copies'
        System.out.println(b.id());

        Book b1 = new Book();
        b1.title = "Book title";
        b1.author = "Author";
        b1.num_copies = 123;
        // The book b exists in memory but isn't saved to the db
        b1.save(); // now the book is in the db
        b1.num_copies = 48; // the book in the db still has 123 copies
        b1.save(); // now the book in the db has 48 copies

        Book b2 = new Book();
        b2.title = "Programming Languages: Build, Prove, and Compare";
        b2.author = "Norman Ramsey";
        b2.num_copies = 777; // hm, same as other book
        b2.save(); // a second record is added to the database
        b2.save();
        b2.save();
        b2.save();

        assert(b.id() != b1.id()); // every row has a globally unique id (int) column, so we can tell them apart
        Book b3 = Model.find(Book.class, b2.id()); // finds the book with id 3 in the db, if any
        assert (b3.id() == Model.find(Book.class, b3.id()).id());

        var list2 = Model.all(TestModel.class);
        for(var item: list2){
            System.out.println(item.getFieldString(item.id(), item));
        }

        b3.title="update Title with the fresh instance";
        b3.save();
        List<Book> bs = Model.all(Book.class); // returns all books in the db

//        b.destroy(); // remove book b from db
    }


    @Test
    public void find () throws IllegalAccessException {
        Model.reset();
        TestModel model1 = new TestModel();
        model1.s = "string";
        model1.i = -10;
        model1.b = false;
        model1.save();

        Book b = new Book();
        b.author = "Author";
        b.title = "title1";
        b.num_copies = 1;
        b.save();

        b = Model.find(b.getClass(),b.id());
        b.author = "Author";
        b.save();
    }
    @Test
    public void save(){
        Model.reset();
        TestModel model1 = new TestModel();
        model1.s = "string";
        model1.i = -10;
        model1.b = false;

        TestModel model2 = new TestModel();
        model2.s = null;
        model2.i = -10;
        model2.b = true;

        TestModel model3 = new TestModel();
        model3.s = "";
        model3.i = 0;
        model3.b = false;

        model1.save();
        model2.save();
        model3.save();

        Book b = new Book();
        b.author = " ";
        b.title = "SWE 04";
        b.num_copies = 1;
        b.save();
    }


    @Test
    public void all() throws IllegalAccessException {
        var list = Model.all(Book.class);
        for(var item: list){
            System.out.println(item.getFieldString(item.id(), item));
        }

        var list2 = Model.all(TestModel.class);
        for(var item: list2){
            System.out.println(item.getFieldString(item.id(), item));
        }
    }


    @Test
    public void save1() { // void save()

        Book b1 = new Book();
        b1.title = "Why Lebron James is the GOAT";
        b1.author = "Bronny James";
        b1.num_copies = 23;
        b1.save();

        assert(b1.id() > 0);

        Book book = Model.find(Book.class, b1.id());
        assertNotNull(book);
        assertEquals(b1.title, book.title);
        assertEquals(b1.author, book.author);
        assert((b1.num_copies == book.num_copies));
    }

    @After
    public void tearDown() throws Exception {
    }
}
