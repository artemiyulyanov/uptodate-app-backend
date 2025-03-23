# UPTODATE v1.0 (dev)

## Stack
- **REST API:** Spring Framework: Boot, Security, JPA
- **Cloud Technologies:** AWS, MinIO
- **Text parsing (JSON2Plain):** FasterXML
- **Utilities:** Lombok

## Common features
This server application implements plenty of useful features that perfectly complement the Front-end. Particularly, it provides a user-friendly model mutations (PUT, PATCH, POST endpoints) that can be easily updated on the Front-end. Besides, it has a well-thought-out model retrieval system (GET endpoints) that does not allow retrieving full model fields but only provides their id for further interaction with other endpoints. This approaches a less loaded interaction with the REST API and makes returning data not much large as it would be
Furthermore, this server application interacts with a MinIO server to store and retrieve model resources, which enables storing large media objects. This is vital for a service like Uptodate, as it is supposed to use media objects constantly.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file or the full text at [MIT License](https://opensource.org/licenses/MIT) for details.

## Overview

"Uptodate v1.0" is the final issue of editing the Backend of the article publishing service as it finally has been complied with an agreed MVP (minimum viable product) version.
All of the issues were committed to comply with common international patterns. It started with a non-compliable version and came to a version that is meticulously detalized and aligned with common patterns. Specifically, there were completely changed the endpoints of the REST API and the way the customer can request to them. 

Prospectly, there will be a Swagger documentation with detailed explanation of the server application endpoints.

## Execution

**You are capable of executing the Backend by using Docker. Keep the further requirements:**
1. Download the project from the Github repository
2. In order to launch the project in the downloaded folder, you need to execute the further command: `docker-compose up --build`
4. The Docker environment is going to be assembled
5. After assembling, please, reboot all the containers
