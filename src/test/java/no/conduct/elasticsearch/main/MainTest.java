package no.conduct.elasticsearch.main;

import no.conduct.elasticsearch.searchutil.Searcher;
import no.conduct.elasticsearch.service.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

/**
 * Created by paalk on 02.09.15.
 */
public class MainTest {

    @Mock
    private Searcher searcher;

    @Mock
    private EventRepositoryService eventRepositoryService;


    private Main main;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void defaultTest() {
        assertTrue(true);
    }
}
