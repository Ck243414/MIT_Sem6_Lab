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
    socklen_t len = sizeof(server_addr);

    // Create the UDP socket
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
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

    // Get input string from the user
    printf("Enter a string to generate permutations: ");
    fgets(buffer, sizeof(buffer), stdin);
    buffer[strcspn(buffer, "\n")] = 0; // Remove the newline character at the end of the input

    // Send the string to the server
    sendto(sockfd, (const char *)buffer, strlen(buffer), 0, (const struct sockaddr *) &server_addr, sizeof(server_addr));

    printf("Waiting for permutations...\n");

    // Receive the permutations from the server
    while (1) {
        int n = recvfrom(sockfd, (char *)buffer, MAX_BUF, 0, (struct sockaddr *) &server_addr, &len);
        buffer[n] = '\0';
        if (strlen(buffer) == 0) {
            break; // Exit if no more data
        }
        printf("Received permutation: %s\n", buffer);
    }

    close(sockfd);
    return 0;
}
