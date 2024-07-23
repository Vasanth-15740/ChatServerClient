import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

class Client extends JFrame implements Runnable, ActionListener {
    private JTextField textField;
    private JTextArea textArea;
    private JButton send;
    private Socket socket;
    private DataInputStream dataInputStream;
    private DataOutputStream dataOutputStream;
    private Thread chat;

    public Client() {

        textField = new JTextField(30);
        textArea = new JTextArea(20, 30);
        textArea.setEditable(false);
        send = new JButton("Send");
        send.addActionListener(this);

        // Apply custom colors
        Color bgColor = new Color(240, 248, 255);
        Color btnColor = new Color(50, 205, 50);

        textArea.setBackground(bgColor);
        textArea.setFont(new Font("Arial", Font.PLAIN, 14));

        textField.setBackground(Color.WHITE);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));

        send.setBackground(btnColor);
        send.setForeground(Color.WHITE);
        send.setFocusPainted(false);

        // Layout setup
        setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout());
        panel.setBackground(bgColor);
        panel.add(textField);
        panel.add(send);

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);

        // Initialize networking
        try {
            socket = new Socket("192.168.71.172", 5000);
            dataInputStream = new DataInputStream(socket.getInputStream());
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize chat thread
        chat = new Thread(this);
        chat.start();

        // JFrame settings
        setTitle("Client");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = textField.getText().trim();
        if (!msg.isEmpty()) {
            textArea.append("Client " + msg + "\n");
            textField.setText("");
            try {
                dataOutputStream.writeUTF(msg);
                dataOutputStream.flush();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                String msg = dataInputStream.readUTF();
                textArea.append("Server: " + msg + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client());
    }
}
