package com.example.intranet.Services;

import com.example.intranet.Dto.PostDTO;
import com.example.intranet.entities.Post;
import com.example.intranet.repositories.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    private final String FOLDER ="D:\\intranet_files\\";

    public String uploadPost(MultipartFile file,PostDTO postDTO ) throws IOException {
        String path = FOLDER+file.getOriginalFilename();
        var post = Post.builder()
                .name(postDTO.getName())
                .type(postDTO.getType())
                .path(path)
                .build();
        postRepository.save(post);
        file.transferTo(new File(path));

        if (post != null){
            return "file uploded successfully"+path;

        }return "failed to upload file";
    }

    private byte[] loadImageData(String filePath) throws IOException {
        return Files.readAllBytes(Paths.get(filePath));
    }


    public PostDTO getPostById(long id) throws IOException {
        Post post = postRepository.getById(id);
        String path =post.getPath();
        byte[] file= Files.readAllBytes(new File(path).toPath());
        var post1 = PostDTO.builder()
                .name(post.getName())
                .type(post.getType())
                .path(post.getPath())
                .fileData(file)
                .build();
        return post1;
    }


    public List<PostDTO> getAllPost() throws IOException {
        List<Post> posts = postRepository.getAllPostByOrderByTimestampDesc();
        List<PostDTO> postDTOS =new ArrayList<>();
        for (Post post : posts){
            PostDTO postDTO = new PostDTO();
            byte[] fileData =loadImageData(post.getPath());
            postDTO.setName(post.getName());
            postDTO.setType(post.getType());
            postDTO.setPath(post.getPath());
            postDTO.setFileData(fileData);
            postDTOS.add(postDTO);
        }
        return postDTOS;
    }


    public void deleteById(long id) {
        postRepository.deleteById(id);
    }

    public void updatePost(PostDTO postDTO,long id,MultipartFile file){
        String path = FOLDER+file.getOriginalFilename();
        Post post = postRepository.getById(id);
        post.setName(postDTO.getName());
        post.setType(postDTO.getType());
        post.setPath(path);
        postRepository.save(post);
    }

}
