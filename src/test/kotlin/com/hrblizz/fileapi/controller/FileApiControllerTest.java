package controller;

import com.hrblizz.fileapi.controller.FileApiController;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;



import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FileApiController.class)
public class FileApiControllerTest {

    private MockMvc mockvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockBean
    private FileApiController fileApiController;


    @BeforeEach
    public void init() {
        this.mockvc = MockMvcBuilders
            .webAppContextSetup(webApplicationContext)
            .build();


    }

    @Test
    public void uploadFileTest() throws Exception {
        Resource fileResource = new FileSystemResource("./test-data/tests.png");
        Assertions.assertNotNull(fileResource);

        MockMultipartFile firstFile = new MockMultipartFile(
            "content",fileResource.getFilename(),
            MediaType.MULTIPART_FORM_DATA_VALUE,
            fileResource.getInputStream());


        Assertions.assertNotNull(fileResource);

        this.mockvc.perform(MockMvcRequestBuilders
                .multipart("/files")
                    .file(firstFile)
                    .param("source", "Timesheet")
                    .param("expireTime", ""))
            .andExpect(status().is(200));
    }

}
