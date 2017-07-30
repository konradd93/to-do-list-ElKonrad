package pl.pollub.unit;

import org.junit.Before;
import org.junit.Test;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import pl.pollub.component.CustomMapper;
import pl.pollub.component.CustomMapperImpl;

import static org.junit.Assert.*;

/**
 * Created by konrad on 29.07.17.
 */
public class UserTest {

    @MockBean
    private CustomMapper customMapper;


    @Before
    public void setup(){
        customMapper = new CustomMapperImpl(new ModelMapper());
    }
}
