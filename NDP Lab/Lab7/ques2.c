#include <stdio.h>

#define REPLENISH_RATE 10240    // Replenishment rate in bytes per second (10 KB = 10240 bytes)
#define BUCKET_SIZE 51200       // Bucket size in bytes (50 KB = 51200 bytes)
#define PACKET_SIZE 15360       // Packet size in bytes (15 KB = 15360 bytes)
#define SEND_INTERVAL 0.5       // Packet send interval in seconds

int main() {
    int tokens = BUCKET_SIZE;  // Initial tokens in the bucket (full bucket)
    int replenishment_rate = REPLENISH_RATE; // Tokens added per second (10 KBps)
    int packet_size = PACKET_SIZE;  // Size of each packet (15 KB)
    float time = 0;              // Simulated time in seconds
    int tokens_left = tokens;    // Remaining tokens in the bucket

    // 1. How many tokens are left in the bucket after 1.5 seconds?
    for (time = 0.5; time <= 1.5; time += SEND_INTERVAL) {
        tokens_left += replenishment_rate * SEND_INTERVAL;  // Add tokens at the replenishment rate

        // Ensure tokens do not exceed the bucket size
        if (tokens_left > BUCKET_SIZE) {
            tokens_left = BUCKET_SIZE;
        }

        // Check if there are enough tokens to send the packet
        if (tokens_left >= packet_size) {
            tokens_left -= packet_size;  // Send the packet, use up tokens
            printf("At t = %.1f seconds, sent a packet of 15 KB. Tokens left: %d bytes\n", time, tokens_left);
        } else {
            printf("At t = %.1f seconds, not enough tokens to send a packet. Tokens left: %d bytes\n", time, tokens_left);
        }
    }
    printf("\nTokens left after 1.5 seconds: %d bytes\n", tokens_left);

    // 2. When will packets start to be queued or dropped?
    tokens_left = BUCKET_SIZE;  // Reset tokens to full bucket
    for (time = 0.5; time <= 10; time += SEND_INTERVAL) {
        tokens_left += replenishment_rate * SEND_INTERVAL;  // Add tokens at the replenishment rate

        // Ensure tokens do not exceed the bucket size
        if (tokens_left > BUCKET_SIZE) {
            tokens_left = BUCKET_SIZE;
        }

        // Check if there are enough tokens to send the packet
        if (tokens_left >= packet_size) {
            tokens_left -= packet_size;  // Send the packet, use up tokens
            printf("At t = %.1f seconds, sent a packet of 15 KB. Tokens left: %d bytes\n", time, tokens_left);
        } else {
            printf("At t = %.1f seconds, not enough tokens to send a packet. Tokens left: %d bytes\n", time, tokens_left);
            break;  // Packets will now be queued or dropped
        }
    }
    printf("\nPackets will start to be queued or dropped at t = %.1f seconds\n", time);

    // 3. Maximum burst size with R = 20 KBps
    int max_rate = 20480;  // New maximum rate in bytes per second (20 KBps = 20480 bytes)
    int max_burst_size = BUCKET_SIZE;  // Maximum burst size is the bucket size (50 KB)
    
    printf("\nMaximum burst size with R = 20 KBps: %d bytes\n", max_burst_size);

    return 0;
}
