package harshwinds.fileRepository.storage;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import harshwinds.fileRepository.error.RepositoryFileNotFoundException;
import harshwinds.fileRepository.model.FileDescriptor;
import harshwinds.fileRepository.repository.RepositoryService;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class FileUploadTests {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RepositoryService repositoryService;
    
    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));
    
    private HttpMessageConverter mappingJackson2HttpMessageConverter;
    
    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
            .findAny()
            .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Test
    public void shouldListAllFiles() throws Exception {
        FileDescriptor fileDescriptor1 = new FileDescriptor();
        fileDescriptor1.setId("1234");
        fileDescriptor1.setFilename("file1.txt");
        fileDescriptor1.setTitle("Title 1");
        fileDescriptor1.setDescription("Description 1");
        fileDescriptor1.setCreationDate(new Date());
        
        FileDescriptor fileDescriptor2 = new FileDescriptor();
        fileDescriptor2.setId("5678");
        fileDescriptor2.setFilename("file2.txt");
        fileDescriptor2.setTitle("Title 2");
        fileDescriptor2.setDescription("Description 2");
        fileDescriptor2.setCreationDate(new Date());
        
        given(this.repositoryService.loadAll())
                .willReturn(Stream.of(fileDescriptor1, fileDescriptor2));

        this.mvc.perform(get("/files/"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(fileDescriptor1.getId())))
                .andExpect(jsonPath("$[0].filename", is(fileDescriptor1.getFilename())))
                .andExpect(jsonPath("$[0].title", is(fileDescriptor1.getTitle())))
                .andExpect(jsonPath("$[0].description", is(fileDescriptor1.getDescription())))
                .andExpect(jsonPath("$[0].creationDate", is(fileDescriptor1.getCreationDate().getTime())))
                .andExpect(jsonPath("$[1].id", is(fileDescriptor2.getId())))
                .andExpect(jsonPath("$[1].filename", is(fileDescriptor2.getFilename())))
                .andExpect(jsonPath("$[1].title", is(fileDescriptor2.getTitle())))
                .andExpect(jsonPath("$[1].description", is(fileDescriptor2.getDescription())))
                .andExpect(jsonPath("$[1].creationDate", is(fileDescriptor2.getCreationDate().getTime())));
    }
    
    @Test
    public void shouldGetFile() throws Exception {
    	String id = "1234";
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setId(id);
        fileDescriptor.setFilename("hello.txt");
        fileDescriptor.setTitle("Title 1");
        fileDescriptor.setDescription("Description 1");
        fileDescriptor.setCreationDate(new Date());
        
        Resource resource = new UrlResource(getClass().getClassLoader()
                .getResource("hello.txt")
                .toURI());
        
        given(this.repositoryService.load(id)).willReturn(fileDescriptor);
        given(this.repositoryService.loadAsResource(id)).willReturn(resource);

        this.mvc.perform(get("/files/" + id))
        		.andExpect(status().isOk())
        		.andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"hello.txt\""))
        		.andExpect(content().string("Hello World!"));
    }
    
    @Test
    public void shouldGetFileDescriptor() throws Exception {
    	String id = "1234";
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setId(id);
        fileDescriptor.setFilename("file1.txt");
        fileDescriptor.setTitle("Title 1");
        fileDescriptor.setDescription("Description 1");
        fileDescriptor.setCreationDate(new Date());
        
        given(this.repositoryService.load(id)).willReturn(fileDescriptor);

        this.mvc.perform(get("/files/" + id + "/descriptor"))
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("$.id", is(fileDescriptor.getId())))
        		.andExpect(jsonPath("$.filename", is(fileDescriptor.getFilename())))
        		.andExpect(jsonPath("$.title", is(fileDescriptor.getTitle())))
        		.andExpect(jsonPath("$.description", is(fileDescriptor.getDescription())))
        		.andExpect(jsonPath("$.creationDate", is(fileDescriptor.getCreationDate().getTime())));
    }

    @Test
    public void shouldSaveUploadedFile() throws Exception {
    	String id = "abc123";
    	String filename = "hello.txt";
        String title = "hello";
        String description = "Saying hello";
        Date creationDate = new Date();
        
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", filename, "text/plain", "Hello World!".getBytes());
        
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setTitle(title);
        fileDescriptor.setDescription(description);
        fileDescriptor.setCreationDate(creationDate);
        
        String fileDescriptorJson = json(fileDescriptor);
        
        MockMultipartFile multipartFileDescriptor =
                new MockMultipartFile("fileDescriptor", "", "application/json", fileDescriptorJson.getBytes());
        
        FileDescriptor uploadedFileDescriptor = new FileDescriptor();
        uploadedFileDescriptor.setId(id);
        uploadedFileDescriptor.setFilename(filename);
        uploadedFileDescriptor.setTitle(title);
        uploadedFileDescriptor.setDescription(description);
        uploadedFileDescriptor.setCreationDate(creationDate);
        
        given(this.repositoryService.store(multipartFile, fileDescriptor)).willReturn(uploadedFileDescriptor);
        
        this.mvc.perform(fileUpload("/files/")
        		.file(multipartFile)
        		.file(multipartFileDescriptor))
                .andExpect(status().isOk())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.id", is(id)))
                .andExpect(jsonPath("$.filename", is(filename)))
                .andExpect(jsonPath("$.title", is(title)))
                .andExpect(jsonPath("$.description", is(description)))
                .andExpect(jsonPath("$.creationDate", is(creationDate.getTime())));

        then(this.repositoryService).should().store(multipartFile, fileDescriptor);
    }
    
    @Test
    public void should400UploadFileWithId() throws Exception {
    	String id = "abc123";
    	String filename = "hello.txt";
        String title = "hello";
        String description = "Saying hello";
        Date creationDate = new Date();
        
        MockMultipartFile multipartFile =
                new MockMultipartFile("file", filename, "text/plain", "Hello World!".getBytes());
        
        FileDescriptor fileDescriptor = new FileDescriptor();
        fileDescriptor.setId(id);
        fileDescriptor.setTitle(title);
        fileDescriptor.setDescription(description);
        fileDescriptor.setCreationDate(creationDate);
        
        String fileDescriptorJson = json(fileDescriptor);
        
        this.mvc.perform(fileUpload("/files/")
        		.file(multipartFile)
        		.contentType(contentType)
        		.content(fileDescriptorJson))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @Ignore // MockMultipartHttpServletRequestBuilder does not support PUT
    public void shouldReuploadedFile() throws Exception {

    }

    @Test
    public void should404WhenMissingFile() throws Exception {
        given(this.repositoryService.loadAsResource("abc123"))
                .willThrow(RepositoryFileNotFoundException.class);

        this.mvc.perform(get("/files/abc123"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    public void should404WhenMissingDescriptor() throws Exception {
        given(this.repositoryService.load("abc123"))
                .willThrow(RepositoryFileNotFoundException.class);

        this.mvc.perform(get("/files/abc123/descriptor"))
                .andExpect(status().isNotFound());
    }
    
    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}