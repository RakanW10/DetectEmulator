#include <jni.h>
#include <string>
#include <android/log.h>
#include <fcntl.h>


// Define ARM64 syscall numbers (replace with actual values)
#define ARM64_OPENAT_SYSCALL 56
#define ARM64_READ_SYSCALL 63
// Inline syscall for cc_openat
int myOpenat(int dirfd, const char *pathname, int flags, mode_t mode) {
    int fd;
    asm volatile (
        "mov x8, %1\n\t"       // syscall number
        "mov x0, %2\n\t"       // dirfd
        "mov x1, %3\n\t"       // pathname
        "mov x2, %4\n\t"       // flags
        "mov x3, %5\n\t"       // mode
        "svc #0x0\n\t"         // software interrupt to trigger syscall
        "mov %0, x0\n\t"       // store syscall return value
        : "=r" (fd)            // output
        : "r" (ARM64_OPENAT_SYSCALL), "r" (dirfd), "r" (pathname), "r" (flags), "r" (mode) // inputs
        : "x0", "x8", "memory" // clobbered registers
        );
    return fd;
}

// Inline syscall for cc_read
ssize_t myRead(int fd, void *buf, size_t count) {
    ssize_t bytes_read;
    asm volatile (
    "mov x8, %1\n\t"       // syscall number (sys_read)
    "mov x0, %2\n\t"       // fd
    "mov x1, %3\n\t"       // buf
    "mov x2, %4\n\t"       // count
    "svc #0x0\n\t"         // software interrupt to trigger syscall
    "mov %0, x0\n\t"       // store syscall return value
    : "=r" (bytes_read)    // output
    : "r" (63), "r" (fd), "r" (buf), "r" (count) // inputs
    : "x0", "memory"       // clobbered registers
    );

    // Check for error and set errno if needed
    if (bytes_read < 0) {
        errno = -bytes_read;
        return -1;
    }
    return bytes_read;
}


extern "C"
JNIEXPORT void JNICALL
Java_live_rakan_detectemulator_MainActivity_test_1syscalls(JNIEnv *env, jobject thiz) {
    int fd = myOpenat(AT_FDCWD,"/proc/self/maps",O_RDONLY,0);
    __android_log_print(ANDROID_LOG_ERROR, "fd", "%d ", fd);

    if(fd == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "File", "The file not opened and status = %d ", fd);
    }
    int isOpen = openat(AT_FDCWD,"/HACKERS_syscall",O_RDONLY);
    __android_log_print(ANDROID_LOG_ERROR, "isOpen", "%d ", isOpen);
    if(isOpen == -1) {
        __android_log_print(ANDROID_LOG_ERROR, "File", "The file not opened and status = %d ", isOpen);
    }


}


extern "C"
JNIEXPORT jint JNICALL
Java_live_rakan_detectemulator_MainActivity_detectEmulator1(JNIEnv *env, jobject thiz) {
    int fd = open("/proc/self/maps", O_RDONLY);
    if (fd == -1) {
        perror("Failed to open /proc/self/maps");
        return 1;
    }

    char buffer[4096];  // Buffer to hold read data
    ssize_t bytes_read;

    while ((bytes_read = myRead(fd, buffer, sizeof(buffer) - 1)) > 0) {
        buffer[bytes_read] = '\0';  // Null-terminate the buffer

        // check the buffer and crash the app if there it is running on emulator
        if (strstr(buffer, "EmulatorConfigOverlay") != NULL) {
            __android_log_print(ANDROID_LOG_ERROR, "Emulator:", "Emulator is detected", buffer);

        }
    }

    if (bytes_read == -1) {
        perror("Error reading /proc/self/maps");
        return 1;
    }


    return 0;
}