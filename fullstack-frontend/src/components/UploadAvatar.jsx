import React, { useRef, useState, forwardRef, useImperativeHandle } from "react";
import axios from "axios";

const apiUrl = process.env.REACT_APP_API_URL;

const UploadAvatar = forwardRef(({ onUpload }, ref) => {
  const fileInputRef = useRef(null);
  const [preview, setPreview] = useState(null);
  const [file, setFile] = useState(null);

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile) {
      setFile(selectedFile);
      setPreview(URL.createObjectURL(selectedFile));
    }
  };

  useImperativeHandle(ref, () => ({
    async uploadAvatar() {
      if (!file) return null;

      const formData = new FormData();
      formData.append("file", file);

      try {
        const response = await axios.post(`${apiUrl}/avatar/upload`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        return response.data; // return fileUrl
      } catch (error) {
        console.error("Upload failed:", error);
        return null;
      }
    },
  }));

  return (
    <div className="mb-3 text-center">
      <input type="file" ref={fileInputRef} onChange={handleFileChange} />
      {preview && (
        <div>
          <img
            src={preview}
            alt="Preview"
            style={{ width: "100px", height: "100px", borderRadius: "50%" }}
            className="mt-2"
          />
        </div>
      )}
    </div>
  );
});

export default UploadAvatar;
