// client.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

int main() {
    int sockfd;
    struct sockaddr_in server_addr;
    char buffer[MAX_BUF];
    
    // Create socket
    if ((sockfd = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    
    // Convert the IP address of the server (localhost in this case) to binary form
    if (inet_pton(AF_INET, "127.0.0.1", &server_addr.sin_addr) <= 0) {
        perror("Invalid address or Address not supported");
        exit(EXIT_FAILURE);
    }

    // Connect to the server
    if (connect(sockfd, (struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Connection failed");
        exit(EXIT_FAILURE);
    }

    // Input string from user
    printf("Input string: ");
    fgets(buffer, sizeof(buffer), stdin);
    buffer[strcspn(buffer, "\n")] = 0;  // Remove the newline character from the input

    // Send the string to the server
    send(sockfd, buffer, strlen(buffer), 0);

    // Receive the sorted numbers from the child process
    int n = recv(sockfd, buffer, sizeof(buffer), 0);
    buffer[n] = '\0';
    printf("Sorted numbers (from child process): %s\n", buffer);

    // Receive the sorted characters from the parent process
    n = recv(sockfd, buffer, sizeof(buffer), 0);
    buffer[n] = '\0';
    printf("Sorted characters (from parent process): %s\n", buffer);

    // Close the socket
    close(sockfd);

    return 0;
}
