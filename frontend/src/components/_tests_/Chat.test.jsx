import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import Chat from "../Chat";
import api from "../../api/axiosInstance";
import "@testing-library/jest-dom";

beforeAll(() => {
  // mock the scrollIntoView function globally so Jest doesn't crash
  window.HTMLElement.prototype.scrollIntoView = jest.fn();
});


// ✅ Mock axios instance
jest.mock("../../api/axiosInstance", () => ({
  post: jest.fn(),
}));

describe("Chat Component", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem("token", "mocked-jwt-token"); // mock logged-in state
  });

  test("renders input field and send button", () => {
    render(<Chat />);

    const input = screen.getByPlaceholderText(/type your message/i);
    const button = screen.getByRole("button", { name: /send/i });

    expect(input).toBeInTheDocument();
    expect(button).toBeInTheDocument();
  });

  test("allows typing and sending message", async () => {
    render(<Chat />);

    const input = screen.getByPlaceholderText(/type your message/i);
    const button = screen.getByRole("button", { name: /send/i });

    // simulate user typing
    await userEvent.type(input, "Hello AI!");
    expect(input).toHaveValue("Hello AI!");

    // mock API response
    api.post.mockResolvedValueOnce({
      data: { message: "Hi there, I am your AI assistant." },
    });

    // click send
    await userEvent.click(button);

    // ✅ Verify API call
    expect(api.post).toHaveBeenCalledWith("/chat", { message: "Hello AI!" });

    // ✅ Wait for AI response to appear
    await waitFor(() =>
      expect(screen.getByText(/Hi there, I am your AI assistant/i)).toBeInTheDocument()
    );
  });

  test("displays error if API call fails", async () => {
    render(<Chat />);

    const input = screen.getByPlaceholderText(/type your message/i);
    const button = screen.getByRole("button", { name: /send/i });

    await userEvent.type(input, "Test message");
    api.post.mockRejectedValueOnce(new Error("Network error"));

    await userEvent.click(button);

    await waitFor(() =>
      expect(screen.getByText(/could not reach the chat service/i)).toBeInTheDocument()
    );
  });
});
