package org.openpnp.web;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import lombok.Getter;

import org.openpnp.spi.Camera;
import org.openpnp.util.ImageUtils;
import org.openpnp.util.OpenCvUtils;

// TODO: We really only want to be encoding a given frame once. Instead of
// each connection getting it's own encoder and FPS we should just pick
// a FPS, use a static table of cameras to encoders and then stream
// frames from that encoder per connection.

@Getter
class MjpegCameraResponse implements StreamingOutput {
    private String boundary = "camera_mjpeg";
    private Camera camera;
    private int framesPerSecond;
    private long bytesWritten;
    private long timeStarted;
    
    public MjpegCameraResponse(Camera camera, int framesPerSecond) {
        this.camera = camera;
        this.framesPerSecond = framesPerSecond;
    }
    
    public Response getResponse() {
        return Response
                .ok(this)
                .header("Connection",  "keep-alive")
                .header("Cache-Control", "no-cache")
                .header("Cache-Control", "private")
                .header("Pragma", "no-cache")
                .header("Content-Type", "multipart/x-mixed-replace; boundary=" + boundary)
                .build();
    }
    
    public double getKbitsPerSecond() {
        double seconds = (System.currentTimeMillis() - timeStarted) / 1000;
        double kBits = (bytesWritten * 8 / 1024.0);
        double kBitsPerSecond = kBits / seconds;
        return kBitsPerSecond;
    }
    
    @Override
    public void write(OutputStream out) throws IOException,
            WebApplicationException {
        out.write(("--" + boundary + "\n").getBytes());
        timeStarted = System.currentTimeMillis();
        while (true) {
            BufferedImage image = camera.capture();
            image = ImageUtils.convertBufferedImage(image, BufferedImage.TYPE_INT_RGB);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            ImageIO.write(image, "jpg", b);
            b.flush();
            b.close();
            byte[] bytes = b.toByteArray();
            
            out.write("Content-Type: image/jpeg\n".getBytes());
            out.write(("Content-Length: " + bytes.length + "\n\n").getBytes());
            out.write(bytes);
            out.write(("\n--" + boundary + "\n").getBytes());
            out.flush();
            bytesWritten += bytes.length;
            
            try {
                Thread.sleep(1000 / framesPerSecond);
            }
            catch (Exception e) {
                
            }
        }
    }
}