#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define SERVER_IP "127.0.0.1"
#define PORT 9090
#define BUFFER_SIZE 1024

int main() {
    int sockfd;
    struct sockaddr_in serv_addr;
    socklen_t addr_size = sizeof(serv_addr);
    char buffer[BUFFER_SIZE];

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(PORT);
    serv_addr.sin_addr.s_addr = inet_addr(SERVER_IP);

    while (1) {
        printf("\n1. Registration Number\n2. Name\n3. Subject Code\n4. Exit\nEnter choice: ");
        fgets(buffer, BUFFER_SIZE, stdin);
        buffer[strcspn(buffer, "\n")] = 0;

        if (strcmp(buffer, "4") == 0) break;

        sendto(sockfd, buffer, strlen(buffer), 0, (struct sockaddr *)&serv_addr, addr_size);
        recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&serv_addr, &addr_size);
        printf("Server Response:\n%s\n", buffer);
    }

    close(sockfd);
    return 0;
}
