// server.c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to swap two characters in a string
void swap(char* a, char* b) {
    char temp = *a;
    *a = *b;
    *b = temp;
}

// Function to generate all permutations of a string
void permute(char* str, int l, int r, struct sockaddr_in* client_addr, int sockfd) {
    if (l == r) {
        // Send the permutation back to the client
        sendto(sockfd, str, strlen(str), 0, (struct sockaddr *)client_addr, sizeof(*client_addr));
        printf("Sent permutation: %s\n", str);
    } else {
        for (int i = l; i <= r; i++) {
            swap((str + l), (str + i));
            permute(str, l + 1, r, client_addr, sockfd);
            swap((str + l), (str + i)); // Backtrack
        }
    }
}

int main() {
    int sockfd;
    struct sockaddr_in server_addr, client_addr;
    char buffer[MAX_BUF];
    socklen_t len = sizeof(client_addr);

    // Create the UDP socket
    if ((sockfd = socket(AF_INET, SOCK_DGRAM, 0)) < 0) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    server_addr.sin_family = AF_INET;
    server_addr.sin_addr.s_addr = INADDR_ANY;
    server_addr.sin_port = htons(PORT);

    // Bind the socket to the port
    if (bind(sockfd, (const struct sockaddr *)&server_addr, sizeof(server_addr)) < 0) {
        perror("Bind failed");
        exit(EXIT_FAILURE);
    }

    printf("Server is listening on port %d...\n", PORT);

    // Receive the string from the client
    int n = recvfrom(sockfd, (char *)buffer, MAX_BUF, 0, (struct sockaddr *) &client_addr, &len);
    buffer[n] = '\0';

    printf("Received string from client: %s\n", buffer);

    // Generate and send all permutations of the string
    permute(buffer, 0, n - 1, &client_addr, sockfd);

    close(sockfd);
    return 0;
}
