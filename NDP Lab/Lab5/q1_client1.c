#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080

int main() {
    int sock;
    struct sockaddr_in server_addr;
    char *message = "Institute Of";
    char buffer[1024];

    // Create client socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Set server address
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");

    // Connect to the server
    if (connect(sock, (struct sockaddr*)&server_addr, sizeof(server_addr)) == -1) {
        perror("Connection failed");
        close(sock);
        exit(EXIT_FAILURE);
    }

    // Send message to server
    send(sock, message, strlen(message), 0);

    // Receive acknowledgment from server
    int bytes_read = recv(sock, buffer, sizeof(buffer), 0);
    buffer[bytes_read] = '\0';  // Null-terminate the string
    printf("Server response: %s\n", buffer);

    // Close the socket
    close(sock);
    return 0;
}

