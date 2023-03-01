<h1>Client-Server Resource Sharing Application</h1>

This is a client-server application that allows publishers to share resources with clients. Publishers can upload their resources to the server, which clients can then search for and download.

The application is built using Java and consists of four main classes: Server, ClientHandler, Client, and Publisher.

The Server class creates a server socket to listen for incoming connections from clients and uses the ClientHandler class to handle client requests. When a client sends a "PUBLISH" request, the server adds the publisher's information to a MySQL database. When a client sends a "LOOKUP" request, the server searches for the requested resource in the database and returns a response containing the available publisher information.

The ClientHandler class is used by the Server class to handle client requests. If the request is a "PUBLISH" request, the class adds publisher information to the database. If the request is a "LOOKUP" request, it searches for the requested resource in the database and returns a response containing the available publisher information.

The Client class is used by clients to search for and download resources. When a client sends a "LOOKUP" request, the server searches for the requested resource and returns a response containing the available publisher information. If the resource is available, the client can then choose a publisher to download from and initiate the download.

The Publisher class is used by publishers to upload their resources to the server. It listens for incoming connections on a port using a nested ThreadedSocket class and sends the requested file to the client. When a publisher uploads a resource, the class establishes a connection with the server and sends a "PUBLISH" request with the file details and IP-port address to be published.

To use the application, simply run the Server class on the server machine and the Client class on the client machines. Publishers can use the Publisher class to upload their resources to the server. The application is flexible and can be used to share various types of resources, such as files, documents, and media.



