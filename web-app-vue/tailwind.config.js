/** @type {import('tailwindcss').Config} */
export default {
    darkMode: 'class',
    content: [
        './src/**/*.{vue,js,ts,jsx,tsx}',
        './index.html',
    ],
    theme: {
        extend: {
            backgroundImage: () => ({
                'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
                'gradient-radial-at-center': 'radial-gradient(at center, var(--tw-gradient-stops))',
            }),
            fontFamily: {
                sans: ["'Montserrat Variable'", 'sans'],
                rubik: ["'Rubik Variable'", 'sans'],
                exo: ["'Exo 2 Variable'", 'sans'],
                montserrat: ["'Montserrat Variable'", 'sans'],
                raleway: ["'Raleway Variable'", 'sans'],
            },
            width: {
                '2p': '2%',
                '3p': '3%',
                '5p': '5%',
                '10p': '10%',
                '15p': '15%',
                '20p': '20%',
                '25p': '25%',
                '30p': '30%',
                '35p': '35%',
                '40p': '40%',
                '45p': '45%',
                '50p': '50%',
                '55p': '55%',
                '60p': '60%',
                '65p': '65%',
                '70p': '70%',
                '75p': '75%',
                '80p': '80%',
                '85p': '85%',
                '90p': '90%',
                '95p': '95%',
                '100p': '100%',
            },
            colors: {
                'slate-250': '#d4dae1',
                light: {
                    'bg-from': '#f6f9fc',
                    'bg-to': '#ececf9',
                    // text: '#4a5462',
                    text: '#334155',

                    'text-1':'#678983'
                },
                dark: {
                    'bg-from': '#1f2937',
                    'bg-to': '#111827',
                    text: '#cfd3d9'
                }
            }
        },
    },
    plugins: [],
}

