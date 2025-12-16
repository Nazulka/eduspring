import React from "react";
import ReactMarkdown from "react-markdown";
import "./SectionView.css";

function SectionView() {
  return (
    <div className="markdown">
      <ReactMarkdown>
        {`
# REST Basics

REST is an **architectural style**.

## Key ideas
- Stateless
- Client–server
- Uniform interface

\`\`\`java
@GetMapping("/users")
public List<User> getUsers() {}
\`\`\`
        `}
      </ReactMarkdown>
    </div>
  );
}

export default SectionView;
