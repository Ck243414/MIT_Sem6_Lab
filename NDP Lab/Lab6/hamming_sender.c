#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <arpa/inet.h>

#define PORT 8080
#define MAX_BUF 1024

// Function to calculate the parity bit
int calculate_parity(int data[], int check_pos, int data_length) {
    int parity = 0;
    for (int i = 0; i < data_length; i++) {
        if ((i + 1) & check_pos) {  // If this bit is checked by the parity bit
            parity ^= data[i];  // XOR the data bit
        }
    }
    return parity;
}

// Function to encode data using Hamming (7,4)
void encode_hamming(int data[], int encoded[]) {
    // Data bits are at positions 3, 5, 6, and 7 (0-based index)
    // Parity bits are at positions 1, 2, and 4 (1-based index)

    encoded[0] = data[0];  // parity bit 1
    encoded[1] = data[1];  // parity bit 2
    encoded[2] = data[2];  // data bit 1
    encoded[3] = data[3];  // parity bit 3
    encoded[4] = data[4];  // data bit 2
    encoded[5] = data[5];  // data bit 3
    encoded[6] = data[6];  // data bit 4

    // Calculate the parity bits
    encoded[0] = calculate_parity(encoded, 1, 7);
    encoded[1] = calculate_parity(encoded, 2, 7);
    encoded[3] = calculate_parity(encoded, 4, 7);
}

// Function to append the parity bits and send data
void send_data(int sock, int data[], int data_length) {
    char buffer[MAX_BUF];
    // Encode the data using Hamming code
    int encoded[7];  // Hamming (7,4) code: 7 bits of encoded data
    encode_hamming(data, encoded);

    // Convert encoded data to a string and send it
    for (int i = 0; i < 7; i++) {
        buffer[i] = encoded[i] + '0';  // Convert integer to char ('0' or '1')
    }
    buffer[7] = '\0';  // Null-terminate the string

    send(sock, buffer, 7, 0);  // Send 7 bits of encoded data to the server
}

int main() {
    int sock;
    struct sockaddr_in server_addr;
    int data[4];

    // Create socket
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) == -1) {
        perror("Socket creation failed");
        exit(EXIT_FAILURE);
    }

    // Set up server address
    server_addr.sin_family = AF_INET;
    server_addr.sin_port = htons(PORT);
    server_addr.sin_addr.s_addr = inet_addr("127.0.0.1");  // Localhost

    // Connect to the server
    if (connect(sock, (struct sockaddr *)&server_addr, sizeof(server_addr)) == -1) {
        perror("Connection failed");
        close(sock);
        exit(EXIT_FAILURE);
    }

    // Input the 4 data bits from the user
    printf("Enter 4 data bits (0 or 1) for transmission:\n");
    for (int i = 0; i < 4; i++) {
        printf("Data bit %d: ", i + 1);
        scanf("%d", &data[i]);
    }

    // Send the encoded data
    send_data(sock, data, 4);

    printf("Data sent successfully with Hamming encoding.\n");

    // Close the socket
    close(sock);
    return 0;
}
