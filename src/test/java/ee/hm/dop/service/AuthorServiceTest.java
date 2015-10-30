package ee.hm.dop.service;

import ee.hm.dop.dao.AuthorDAO;
import ee.hm.dop.model.Author;
import org.easymock.EasyMockRunner;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;

@RunWith(EasyMockRunner.class)
public class AuthorServiceTest {

    @TestSubject
    private AuthorService authorService = new AuthorService();

    @Mock
    private AuthorDAO authorDAO;

    @Test
    public void createAuthor() {
        Author author = new Author();
        author.setName("firstName");
        author.setSurname("lastName");

        expect(authorDAO.create(anyObject(Author.class))).andReturn(author);

        replay(authorDAO);

        Author returned = authorService.createAuthor(author.getName(), author.getSurname());

        verify(authorDAO);

        assertEquals(author.getName(), returned.getName());
        assertEquals(author.getSurname(), returned.getSurname());
    }
}
