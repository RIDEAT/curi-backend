<div align="center">


<h3 align="center">Backend for Curi</h3>

 
</div>


### Installation

1. Clone the repo
   ```sh
   git clone https://github.com/RIDEAT/curi-backend.git
   ```
2. Move to it
   ```sh
   cd curi_backend
   ```

3. Local Build
   ```sh
   ./gradlew build
   ```
   
4. Docker Build
    
    ```sh
    // For mac
    docker build . -t springbootapp --platform linux/amd64
    ```
    ```sh
    // For window
    docker build . -t springbootapp 
    ```
5. Docker Run
    ```sh
    docker run springbootapp
    ```
Followings are options that you can add! 
* Port option(-p)
  <br>Connect between local network and container inner network.

* Volume option(-v)
<br>Enable inner container to use local files 

### Reference 
[Here](https://ttl-blog.tistory.com/761 ) is the website that I referred to when building 

### Contact
If you have any problems, feel free to contact 8514199@gmail.com

