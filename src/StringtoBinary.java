class StringtoBinary {

    public static String strToBinary(String s)
    {
        byte[] bytes = s.getBytes();
        StringBuilder bin = new StringBuilder();
        for (byte b : bytes)
        {
            int val = b;
            for (int i = 0; i < 8; i++)
            {
                bin.append((val & 128) == 0 ? 0 : 1);
                val <<= 1;
            }
        }
        return bin.toString();
    }
}
