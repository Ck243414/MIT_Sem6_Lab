#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 9090
#define BUFFER_SIZE 1024

void process_request(int option, char *response) {
    if (option == 1) {
        sprintf(response, "Name: John Doe\nAddress: 123 University St\nPID: %d", getpid());
    } else if (option == 2) {
        sprintf(response, "Department: CS\nSemester: 5\nSection: A\nPID: %d", getpid());
    } else if (option == 3) {
        sprintf(response, "Marks: 85/100\nPID: %d", getpid());
    } else {
        strcpy(response, "Invalid Option");
    }
}

int main() {
    int sockfd;
    struct sockaddr_in serv_addr, cli_addr;
    socklen_t addr_size = sizeof(cli_addr);
    char buffer[BUFFER_SIZE], response[BUFFER_SIZE];

    sockfd = socket(AF_INET, SOCK_DGRAM, 0);
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_addr.s_addr = INADDR_ANY;
    serv_addr.sin_port = htons(PORT);

    bind(sockfd, (struct sockaddr *)&serv_addr, sizeof(serv_addr));
    printf("UDP Server listening on port %d...\n", PORT);

    while (1) {
        recvfrom(sockfd, buffer, BUFFER_SIZE, 0, (struct sockaddr *)&cli_addr, &addr_size);
        int option;
        sscanf(buffer, "%d", &option);
        process_request(option, response);
        sendto(sockfd, response, strlen(response), 0, (struct sockaddr *)&cli_addr, addr_size);
    }

    close(sockfd);
    return 0;
}
